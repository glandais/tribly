-- docs/plans/2026-10-05-weather.md §2: the Open-Meteo forecast cache behind the ride weather. New
-- tables only: the previous release, which knows none of them, keeps running beside this one during a
-- start-first deploy.
--
-- Global on purpose, with no domain_id: a forecast is a function of a place and an hour, nothing
-- else, and it is only ever read from a ride the caller may already read. The rows hold the centre of
-- a ~5 km cell, never a meeting point.

-- One forecast location: a 0.05° cell, and an elevation band of 100 m when the point has a known
-- altitude (route samples). The departure point has none (ele_band null): the provider's own DEM
-- then applies. Refreshed by WeatherFetchWorker when next_refresh_at is due, planned by
-- WeatherPlanner, purged by WeatherHousekeeping once nothing asked for it for two days.
create table weather_cells
(
    id              bigint                      not null,
    lat_idx         integer                     not null,
    lon_idx         integer                     not null,
    ele_band        integer,
    latitude        double precision            not null,
    longitude       double precision            not null,
    grid_elevation  double precision,
    timezone        varchar(64),
    fetched_at      timestamp(6) with time zone,
    next_refresh_at timestamp(6) with time zone not null,
    nearest_need_at timestamp(6) with time zone,
    last_demand_at  timestamp(6) with time zone not null,
    claimed_until   timestamp(6) with time zone,
    attempts        integer                     not null,
    last_error      varchar(500),
    created_at      timestamp(6) with time zone not null,
    updated_at      timestamp(6) with time zone not null,
    primary key (id)
);
-- The cell key. NULLS NOT DISTINCT: the departure cell (no elevation band) must be one row too, or
-- the planner's ON CONFLICT would insert a new one every tick.
create unique index uk_weather_cells_key on weather_cells (lat_idx, lon_idx, ele_band) nulls not distinct;
create index idx_weather_cells_next_refresh on weather_cells (next_refresh_at);
create index idx_weather_cells_last_demand on weather_cells (last_demand_at);

-- One forecast hour of a cell. Rewritten whole at every refresh (ON CONFLICT DO UPDATE), purged a day
-- after the hour has passed.
create table weather_hourly
(
    cell_id                   bigint                      not null,
    time                      timestamp(6) with time zone not null,
    temperature               double precision            not null,
    apparent_temperature      double precision            not null,
    precipitation_probability integer,
    precipitation             double precision            not null,
    weather_code              integer                     not null,
    wind_speed                double precision            not null,
    wind_direction            double precision            not null,
    wind_gusts                double precision,
    primary key (cell_id, time)
);
alter table weather_hourly
    add constraint fk_weather_hourly_cell foreign key (cell_id) references weather_cells on delete cascade;
create index idx_weather_hourly_time on weather_hourly (time);

-- Sunrise and sunset of a cell, per local date (the cell's own time zone). Null in polar day or night.
create table weather_daily
(
    cell_id bigint not null,
    date    date   not null,
    sunrise timestamp(6) with time zone,
    sunset  timestamp(6) with time zone,
    primary key (cell_id, date)
);
alter table weather_daily
    add constraint fk_weather_daily_cell foreign key (cell_id) references weather_cells on delete cascade;
create index idx_weather_daily_date on weather_daily (date);
