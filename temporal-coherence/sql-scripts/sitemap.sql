-- create the schema of the table
--   Each row shows how many urls have location attribute (loc column),
--   how many urls have last modified attribute, how many changes hourly
--   (hourly attribute), etc.
-- 
--   I hope that the column names are self explanatory (loc: location,
--   npriorities: how many different priority values there were in the site

CREATE TABLE sitemaps
(
  rowno integer,
  url character varying(500),
  loc integer,
  lastmod integer,
  priority integer,
  changeall integer,
  "always" integer,
  hourly integer,
  daily integer,
  weekly integer,
  montly integer,
  yearly integer,
  never integer,
  npriorities integer
)

-- import the data into the table (postgres) on unix:
$ gunzip -c sitemaps-sites.txt.gz | \
    psql -h hostName -d dbName \
    -c "copy sitemaps from stdin  using DELIMITERS ' '"

-- check the sites that have most pages and most change classes:
-- score indicates how many change classes were present in the file

select * from (
select url, always1+ hourly1+ daily1+ weekly1+ montly1+ yearly1+ never1
  as score, changeall, always, hourly, daily, weekly, montly, yearly, never,
  npriorities
from (
SELECT 	*,
    case when changeall <> always and always <>0 then 1 else 0 end as always1,
    case when changeall <> hourly and hourly <>0 then 1 else 0 end as hourly1,
    case when changeall <> daily  and daily  <>0 then 1 else 0 end as daily1,
    case when changeall <> weekly and weekly <>0 then 1 else 0 end as weekly1,
    case when changeall <> montly and montly <>0 then 1 else 0 end as montly1,
    case when changeall <> yearly and yearly <>0 then 1 else 0 end as yearly1,
    case when changeall <> never  and never  <>0 then 1 else 0 end as never1
from sitemaps) as x
order by  score desc, changeall desc) as y
where score >= 2

