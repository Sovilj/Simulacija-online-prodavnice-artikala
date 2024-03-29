USE [Projekat]
GO
/****** Object:  StoredProcedure [dbo].[SP_Pomoc]    Script Date: 19-Jun-19 15:46:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[SP_Pomoc]
    @nodeCA int,
    @nodeCB int,
	@nodeDist int,
	@city2 int,
	@newRoute varchar(MAX),
	@newRouteDist int,
	@route varchar(MAX) output,
	@routeDist int output,
	@RetVal int output
AS
begin

	declare @graph table (
		PA varchar (20),
		PB varchar (20),
		Distance INT
	)
	insert into @graph 
	select cityID1,cityID2,distance
	from connected
	insert into @graph 
	select cityID2,cityID1,distance
	from connected

		set @RetVal = 0 --nije nadjen

		declare @kursor3 cursor
		declare @node2CA int
		declare @node2CB int
		declare @node2Dist int
		declare @bool int 

		declare @prihvatniRoute varchar(MAX)
		declare @prihvatniRouteDist int

		declare @tempRoute varchar(MAX)
		declare @tempRouteDist int

		set @kursor3 = cursor for
		select top 1 PA,PB,distance from @graph
		where PA = @nodeCB and PB != @nodeCA
		order by newid()

		open @kursor3

		fetch next from @kursor3
		into @node2CA,@node2CB,@node2Dist
		
		-- za dati cvor pogledamo sve njegove sledbenike
		while @@FETCH_STATUS = 0
		begin
			-- proverimo da li je ovo cvor kraj ako jeste vracamo nazad
			if (@node2CB = @city2)begin
				set @route =  cast ( @newRoute +'-'+ cast ( @node2CB as varchar(20)) as varchar(max))
				set @routeDist = @newRouteDist + @node2Dist
				set @RetVal = 1 --uspeh
				return
			end
			-- ako je na putu dodam ga i pozivam za njega sve komsije
			if (patindex( '%'+cast(@node2CB as varchar(3))+'%',cast( @newRoute as varchar(max)) ) > 0 )
			begin
				-- ako je vec na putu
				set @route = @newRoute
				set @routeDist = @newRouteDist
				set @RetVal = 0
				return 
			end else
			begin
				set @tempRoute =  cast ( @newRoute +'-'+ cast ( @node2CB as varchar(20)) as varchar(max))
				set @tempRouteDist = @newRouteDist + @node2Dist

				exec dbo.SP_Pomoc @node2CA,@node2CB,@node2Dist,@city2,@tempRoute,@tempRouteDist,
				@route = @prihvatniRoute output,
				@routeDist = @prihvatniRouteDist output,
				@RetVal = @bool output
				
				if (@bool = 1)begin
					set @route =  @prihvatniRoute
					set @routeDist = @prihvatniRouteDist
					set @RetVal = 1
					return
				end
			end 
					
		fetch next from @kursor3
		into @node2CA,@node2CB,@node2Dist
		end

		 
		


end