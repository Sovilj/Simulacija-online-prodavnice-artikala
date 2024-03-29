USE [Projekat]
GO
/****** Object:  UserDefinedFunction [dbo].[FN_Time_ArticleLocations]    Script Date: 19-Jun-19 15:47:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER FUNCTION [dbo].[FN_Time_ArticleLocations]
(
    @orderID int
)
RETURNS table
AS
return 
select s.cityID
from Shop s, Article a, Item i
where s.shopID = a.shopID and 
	  a.artID = i.artID and
	  i.orderID = @orderID