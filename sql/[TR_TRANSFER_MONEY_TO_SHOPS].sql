USE [Projekat]
GO
/****** Object:  Trigger [dbo].[TR_TRANSFER_MONEY_TO_SHOPS]    Script Date: 19-Jun-19 15:45:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER TRIGGER [dbo].[TR_TRANSFER_MONEY_TO_SHOPS]
    ON [dbo].[Orders]
    FOR UPDATE
    AS
    BEGIN
	
	declare @inState varchar(100)
	declare @OldState varchar(100)

	declare @orderID int
	declare @itemID int
	declare @buyerID int
	declare @RecTime datetime
	declare @tax int

	set @tax = 5
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
		if ( @inState = 'arrived' and @OldState = 'sent') 
		begin
			
			--posiljka je stigla treba napraviti transakciju i dati pare radnjama
			-- prvo pogledati da li buyer ima uslov da ima popust od 2%
			select @RecTime = ReceivedTime
			from orders
			where orderID = @orderID
			-- treba updateovati i order received  time
			-- received se updateuje kad i arrived state u time Trigeru 
			--update Orders set receivedTime = @CurrTime where orderID = @orderID

			select @buyerID = buyerID
			from Orders 
			where orderID = @orderID

			declare @timeWind datetime

			
			 --oduyimanje 30 dana od trenutnog datuma radi daljeg poredjenja
			set @timeWind = dateadd(day,-30,@RecTime)

			print(concat('timewind = ',@timeWind))

			if exists (select *
			from Transactions 
			where buyerID = @buyerID and Type = 'BO' and
					exeTime > @timeWind and exeTime < @RecTime and amount > 10000)
				begin
					set @tax = 3
				end
			
			-- prolaz kroz iteme ordera

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
			declare @shopID int
			declare @systemProfit decimal(10,3)
			declare @check int 
			set @sumAll = 0

			while @@FETCH_STATUS = 0
			begin 

				SELECT @sum = cast (cast (a.price*i.amount*(100-s.discount) as decimal(10,3)) 
							/ cast (100 as decimal(10,3)) as decimal (10,3)) ,
							@shopID = s.shopID					
				FROM Item i, Article a,Shop s 
				WHERE i.ItemID = @itemID and a.artID = i.artID and s.shopID = a.shopID

				set @systemProfit = cast( cast (@sum * @tax as decimal(10,3))/ cast (100 as decimal(10,3)) as decimal(10,3))
				set @sum =cast( cast (@sum * (100-@tax) as decimal(10,3))/ cast (100 as decimal(10,3)) as decimal(10,3))
	
				update shop
				set account = account + @sum
				where shopID = @shopID

				-- nova transakcija

				print(concat('shop gets = ',@sum))
				print(concat('Time = ',@RecTime))
				print(concat('orderID = ',@orderID))
				print(concat('shopID = ',@shopID))
				print(concat('systemProfit + = ',@systemProfit))

				insert into Transactions (amount,exeTime,orderID,shopID,Type) 
				VALUES (@sum,@RecTime,@orderID,@shopID,'SO')

				-- dodajemo na profit sistema 
				if exists (select * from systemProfit)
					update systemProfit set profit += @systemProfit where systemID = 1
				else 
					insert into systemProfit(systemID,profit) values (1,@systemProfit)

				



				fetch next from @kursorP
				into @itemID
			end
			close @kursorP
			deallocate @kursorP
		
	
		


		end else 
			return
			

	end
	close @kursor
	deallocate @kursor
	

    END
