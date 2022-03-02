CREATE TABLE warnings
(
    device_id      integer   not null
        constraint warnings_pk primary key,
    time           timestamp not null,
    latitude       double,
    longitude      double    not null,
    danger_level   VARCHAR   not null,
    sensor_value   double    not null
)