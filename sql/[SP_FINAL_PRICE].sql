USE [Projekat]
GO
/****** Object:  StoredProcedure [dbo].[SP_FINAL_PRICE]    Script Date: 19-Jun-19 15:46:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[SP_FINAL_PRICE]
    @orderID int ,
    @price decimal(10,3) OUTPUT 
AS
    declare @kursorP cursor
	declare @itemID int

	set @kursorP = cursor for 
	select ItemID
	from Item
	where orderID = @orderID

	open @kursorP

	fetch next from @kursorP
	into @itemID
			
	declare @sum decimal(10,3)
	declare @sumAll decimal(10,3)

	set @sumAll = 0

    while @@FETCH_STATUS = 0
	begin 

		SELECT @sum = cast (cast (a.price*i.amount*(100-s.discount) as decimal(10,3)) 
					/ cast (100 as decimal(10,3)) as decimal (10,3)) 
		FROM Item i, Article a,Shop s 
	    WHERE i.ItemID = @itemID and a.artID = i.artID and s.shopID = a.shopID

		set @sumAll = @sumAll + @sum

		fetch next from @kursorP
		into @itemID
	end
    close @kursorP
    deallocate @kursorP
			
	set @price = @sumAll
RETURN 0 