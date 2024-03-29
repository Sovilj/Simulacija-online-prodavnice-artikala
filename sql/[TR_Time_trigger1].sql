USE [Projekat]
GO
/****** Object:  Trigger [dbo].[TR_Time_trigger1]    Script Date: 19-Jun-19 15:45:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER TRIGGER [dbo].[TR_Time_trigger1]
ON [dbo].[Time]
FOR INSERT, UPDATE
AS
BEGIN
    
	declare @ArtCityID int
	declare @orderID int
	declare @OrderCurrLoc int
	declare @buyerID int
	declare @buyersLoc int
	declare @CurrTime datetime
	declare @kursor1 cursor
	declare @sentTime datetime

	set @kursor1 = cursor for
	select orderID from Orders

	open @kursor1 

	fetch next from @kursor1
	into @orderID 

	--slektujem vreme
	select @CurrTime = time from inserted


	while @@FETCH_STATUS = 0
	begin

		-- treba postaviti location

		-- prvo proveriti da li su svi itemi skupljeni
				
			--id kupca
			select @buyerID = buyerID from Orders where orderID = @orderID
			--lokacija kupca
			select @buyersLoc = cityID from Buyer where buyerID = @buyerID
			-- vreme slanja 
			select @sentTime = sentTime from Orders where orderID = @orderID
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
			--	print(concat('RASTOJANJE = ',@rast,'ruta = ',@route))

				if ( @rast < @minRast) begin
					set @minRast = @rast
					set @Closest = @ItemCity
			--		print(concat('u ifu  = ',@minRast,'...',@ItemCity))
				end

				fetch next from @kursor2
				into @ItemCity 

			end
			close @kursor2
			deallocate @kursor2
		
			if (@minRast != 1000 and (ISNULL(@sentTime,0)!=0))
			begin
				update Orders set location = @Closest where orderID = @orderID
				print(concat('UPIS CLOSEST = ',@Closest,'order = ',@orderID))
			end 
				
			-- sada bi trebalo da znamo koji je grad najblizi buyeru 
			
			-- treba proveriti da li su itemi stigli u taj grad
			-- Item location se ne menja pa uvek moze ova provera da se radi 
			-- posto u item location je lokacija shopa artikla

			declare @TimeDiff int
			declare @TimeDiffK2 int
			declare @FoundRast int
			declare @AllItemsInClosest int
			declare @maxRast int
			declare @kursor3 cursor

			set @AllItemsInClosest = 0
			set @FoundRast = 0
			set @maxRast = 0

			if (ISNULL(@sentTime,0)!=0)begin
			print(concat('SENT TIME = ',@sentTime))
			--koliko je proslo od sentTime do sada
			set @TimeDiff = datediff(day,@sentTime,@currTime) + 1 -- +1 !!!!!
			print(concat('TIMEDIFF = ',@TimeDiff))
			--zavrtimo petlju svih itema u orderu
				
			set @kursor3 = cursor for
			select location
			from item
			where orderID = @orderID

			open @kursor3

			fetch next from @kursor3
			into @ItemCity 

			while @@FETCH_STATUS = 0 
			begin
			
				-- trazim rastojanje izmedju itema i closest grada
				-- i proveraamo da li je to rastojanje predjeno u odnosu na 
				-- currTime - sentTime
				exec dbo.SP_RastojanjeDvaGrada @Closest,@ItemCity,
				@rastojanje = @rast OUTPUT,
				@ruta = @route output

				if ( @rast > @maxRast ) begin
					set @maxRast = @rast
					print(concat('max rast = ',@ItemCity))
					print(concat('ROUTEEEEEEEEEEEEE KOD TRAZENJA MAX ROUT @@route = ',@route))
				end
	
				fetch next from @kursor3
				into @ItemCity 

			end
			close @kursor3
			deallocate @kursor3

			if (@maxRast < abs( @TimeDiff ) ) -- da li je sastavljena porudzbina , gotov k1
			begin
				print(concat('SASTAVLJENA PORUDZBINA = ',@orderID))
				print(concat('-------------------------------------------------------------------------------------------------------------',@orderID))
				-- update Orders set location = -2 where orderID = @orderID --privremeno
				-- itemi su stigli u @Closest znaci gotov je korak 1
				-- Racunamo sad rastojanje za K2
				--porudzbina je vec stigla ako je ovo tacno i treba namestiti i recevied time ali tako da je ono jednako maxRast
				if (@Closest = @buyersLoc)begin
					declare @recT datetime
					set @recT = dateadd(day,@maxRast,@sentTime)
					update Orders set state='arrived', receivedTime = @recT	 where orderID = @orderID
				end else begin

					set @TimeDiffK2 = @TimeDiff - @maxRast -- koliko je proslo od sastavljanja
					print(concat('AAAAAAAAAAAAAAAAA @TimeDiffK2 = ',@TimeDiffK2))
					-- treba naci sledeci grad na putu izmedju @Closest i @BuyerLoc

					exec dbo.SP_RastojanjeDvaGrada @Closest,@buyersLoc,
					@rastojanje = @rast OUTPUT,
					@ruta = @route output

					declare @recivedT datetime
					set @recivedT = dateadd(day,@rast+@maxRast,@sentTime)

					print(concat('ROUTEEEEEEEEEEEEE @@route = ',@route))
					print(concat('-------------------------------------------------------------------------------------------------------------',@orderID))

					declare @skinut varchar(max)
					declare @ostatak varchar(max)
					-- skidamo Closest koji je trenutno na ruti (trebalo bi)
					set @skinut = substring ( @route ,1, PATINDEX('%-%',@route)-1 )
					set @ostatak = substring ( @route , len(@skinut)+2,len(@route) )
					-- sada u skinut dobijamo grad koji je sledeci na ruti
					set @skinut = substring ( @ostatak ,1, PATINDEX('%-%',@ostatak)-1 )
					set @ostatak = substring ( @ostatak , len(@skinut)+2,len(@ostatak) )
				
					print(concat('PRE WHILE skinut = ',@skinut))
					print(concat('PRE WHILE @ostatak = ',@ostatak))
					print(concat('PRE WHILE len(@ostatak) = ',len(@ostatak)))
				
					while (@skinut != 0)begin
					
						-- merimo rastojanje do njega
						exec dbo.SP_RastojanjeDvaGrada @Closest,@skinut,
						@rastojanje = @rast OUTPUT,
						@ruta = @route output

						if (@rast <= @TimeDiffK2)begin
							update Orders set location = @skinut where orderID = @orderID
						end else begin
							break -- nije stiglo do prvog grada ne potrebe dalje ici 
						end
						-- da li je ovaj grad krajnji
						if (@skinut = @buyersLoc)begin
							update Orders set state='arrived', receivedTime = @recivedT where orderID = @orderID
							break;
						end
						if (PATINDEX('%-%',@ostatak)-1 <= 0)begin
							set @skinut = @ostatak
							set @ostatak = ''
						end else begin
							set @skinut = substring ( @ostatak ,1, PATINDEX('%-%',@ostatak)-1 )				
							set @ostatak = substring ( @ostatak , len(@skinut)+2,len(@ostatak) )
						end

						print(concat('kraj WHILE skinut = ',@skinut))
						print(concat('kraj WHILE @ostatak = ',@ostatak))
						print(concat('kraj WHILE len(@ostatak) = ',len(@ostatak)))
					end
				end
			end
			end 
			fetch next from @kursor1
			into @orderID 
		
	end
	close @kursor1
	deallocate @kursor1

END
