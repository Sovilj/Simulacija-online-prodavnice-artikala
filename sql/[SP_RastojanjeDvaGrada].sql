USE [Projekat]
GO
/****** Object:  StoredProcedure [dbo].[SP_RastojanjeDvaGrada]    Script Date: 19-Jun-19 15:47:00 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[SP_RastojanjeDvaGrada]
    @city1 int,
    @city2 int,
	@rastojanje int output,
	@ruta varchar(max) output
AS
begin
	declare @routes table (
		line varchar(MAX),
		dist int
	)
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

	declare @kursor1 cursor
	declare @start int 
	declare @c2 int
	declare @dist int
	declare @route varchar(MAX)
	declare @routeDist int

	set @routeDist = 0

	if (@city1 = @city2)
		insert into @routes values ('-',0)

	set @kursor1 = cursor for
	select PA,PB,distance from @graph
	where PA = @city1

	open @kursor1

	fetch next from @kursor1
	into @start,@c2,@dist

	while @@FETCH_STATUS = 0
	begin
		
		set @route = cast ( cast ( @start as varchar(20))+'-'+cast (@c2 as varchar(20))  as varchar(max))
		set @routeDist = @dist

		if (@c2 = @city2)begin
			insert into @routes values (@route,@routeDist)
			break;
		end

		declare @kursor2 cursor
		declare @nodeCA int
		declare @nodeCB int
		declare @nodeDist int
		declare @bool int
		declare @whileCount int

		declare @prihvatniRoute varchar(MAX)
		declare @prihvatniRouteDist int

		set @kursor2 = cursor for
		select top 1 PA,PB,distance from @graph
		where PA = @c2 and PB != @start
		order by newid()

		open @kursor2

		fetch next from @kursor2
		into @nodeCA,@nodeCB,@nodeDist
		set @whileCount = 50
		while @@FETCH_STATUS = 0
		begin
			set @route =  cast ( @route +'-'+ cast ( @nodeCB as varchar(20)) as varchar(max))
			set @routeDist = @routeDist + @nodeDist
			
			if (@nodeCB = @city2)begin
				insert into @routes values (@route,@routeDist)
				break;
			end
			while (@whileCount != 0 ) begin

				exec dbo.SP_Pomoc @nodeCA,@nodeCB,@nodeDist,@city2,@Route,@RouteDist,
				@route = @prihvatniRoute output,
				@routeDist = @prihvatniRouteDist output,
				@RetVal = @bool output
			
				if (@bool = 1 and not exists (select * from @routes where line = @prihvatniRoute and dist = @prihvatniRouteDist))begin
					insert into @routes values (@prihvatniRoute,@prihvatniRouteDist)
				end
				set @whileCount = @whileCount - 1
			end
			fetch next from @kursor2
			into @nodeCA,@nodeCB,@nodeDist
		end

		fetch next from @kursor1
		into @start,@c2,@dist
	end
	 
	select @rastojanje = dist , @ruta = line
	from @routes
	order by dist desc

	
	
	


end