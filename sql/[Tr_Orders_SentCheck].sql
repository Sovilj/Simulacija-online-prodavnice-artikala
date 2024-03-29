USE [Projekat]
GO
/****** Object:  Trigger [dbo].[Tr_Orders_SentCheck]    Script Date: 19-Jun-19 15:44:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER TRIGGER [dbo].[Tr_Orders_SentCheck]
    ON [dbo].[Orders]
    FOR INSERT, UPDATE
    AS
    BEGIN
    
	declare @inState varchar(100)
	declare @OldState varchar(100)

	declare @orderID int
	declare @itemID int
	declare @buyerID int
	declare @CurrTime datetime
	declare @kursor cursor

	set @kursor = cursor for 
	select orderID,state
	from inserted 

	select @oldState = state
	from deleted

	open @kursor 

	fetch next from @kursor
	into @orderID,@inState

	while @@FETCH_STATUS = 0
	begin 
		if ( @inState = 'sent' and @OldState = 'created') 
		begin
			-- ako se stanje promenilo na sent radimo proveru

			--slektujem vreme
			if (exists (select time from Time where timeID = 1) )
			begin
				select @CurrTime = time
				from Time
				where timeID = 1
			end else
			begin
				set @CurrTime = '00:00';
			end
			-- treba updateovati i order sent  time

			update Orders set sentTime = @CurrTime where orderID = @orderID


			declare @kursorI cursor
			declare @amount int
			declare @count int
			--selektuje buyerID
			select @buyerID = buyerID
			from Orders
			where orderID = @orderID
			
			--pretraga svih itema i artikala i provera count i amount
			set @kursorI = cursor for 
			select ItemID
			from Item
			where orderID = @orderID

			open @kursorI

			fetch next from @kursorI
			into @itemID

			while @@FETCH_STATUS = 0
			begin 

				select @count = count
				from Article a, Item i 
				where a.artID = i.artID and i.itemID = @itemID 

				select @amount = amount
				from Item 
				where itemID = @itemID 

				if (@amount > @count)
				begin
					print('Not enough articles in shop for this order')
					rollback transaction
					
					return
				end
				
				fetch next from @kursorI
				into @itemID
			end
			close @kursorI
			deallocate @kursorI

			-- provereno je da ima artikala u prodavnicama

			-- racunanje finalPrice
			declare @kursorP cursor

			set @kursorP = cursor for 
			select ItemID
			from Item
			where orderID = @orderID

			open @kursorP

			fetch next from @kursorP
			into @itemID
			
			declare @sum decimal(10,3)
			declare @sumAll decimal(10,3)
			declare @ItemAmount int
			declare @artID int

			set @sumAll = 0
			
			while @@FETCH_STATUS = 0
			begin 
				
				SELECT @sum = cast (cast (a.price*i.amount*(100-s.discount) as decimal(18,3)) 
							/ cast (100 as decimal(10,3)) as decimal (10,3)) ,
						@ItemAmount = i.amount ,
						@artID = a.artID
				FROM Item i, Article a,Shop s 
				WHERE i.ItemID = @itemID and a.artID = i.artID and s.shopID = a.shopID


				set @sumAll = @sumAll + @sum

				--skidanje sa article count

				update Article 
				set count = count - @ItemAmount
				where artID = @artID

				
				fetch next from @kursorP
				into @itemID
			end
			close @kursorP
			deallocate @kursorP
			
			-- podaci
			print(concat('FinalPRice = ',@sumAll))
			print(concat('TIme = ',@currTime))
			print(concat('orderID = ',@orderID))
			print(concat('buyerID = ',@buyerID))

			--skidanje sa buyer racuna

			update Buyer 
			set account = account - @sumAll
			where buyerID = @buyerID

			-- pravljenje nove transakcije

			insert into Transactions (amount,exeTime,orderID,buyerID,Type) 
			VALUES (@sumAll,@CurrTime,@orderID,@buyeriD,'BO')
-------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------

			-- DODAVANJE LOCATION
			-- treba naci najblizi grad kupcu 

			-- prvo proveriti da li su svi itemi skupljeni
			declare @buyersLoc int

			--id kupca
			select @buyerID = buyerID from Orders where orderID = @orderID
			--lokacija kupca
			select @buyersLoc = cityID from Buyer where buyerID = @buyerID
	
			-- najblizi grad kupcu
			declare @kursor2 cursor
			declare @ItemCity int
			declare @rast int
			declare @route varchar(max)
			declare @minRast int
			declare @Closest int
		
			-- selektujemo lokacije pojedinacnih itema
			set @kursor2 = cursor for
			select location
			from item
			where orderID = @orderID

			open @kursor2

			fetch next from @kursor2
			into @ItemCity 

			set @minRast = 1000

			while @@FETCH_STATUS = 0 
			begin
			
				-- trazim rastojanje izmedju itema i grada kupca
				exec dbo.SP_RastojanjeDvaGrada @buyersLoc,@ItemCity,
				@rastojanje = @rast OUTPUT,
				@ruta = @route output
				print(concat('RASTOJANJE = ',@rast,'ruta = ',@route))

				if ( @rast < @minRast) begin
					set @minRast = @rast
					set @Closest = @ItemCity
					print(concat('u ifu  = ',@minRast,'...',@ItemCity))
				end

				fetch next from @kursor2
				into @ItemCity 

			end
			close @kursor2
			deallocate @kursor2
		
			if (@minRast = 1000 )
			begin
				print('Nisu povezani gradovi')
			end else
			begin
			print(concat('Najblizi grad kupcu',@Closest))

			update Orders set location = @Closest where orderID = @orderID
			
			end
			
			
			
			
		end else 

			return
			
		fetch next from @kursor
		into @orderID,@inState
	end
	close @kursor
	deallocate @kursor
	

	

    END
