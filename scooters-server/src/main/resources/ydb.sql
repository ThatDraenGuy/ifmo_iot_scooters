CREATE TABLE scooter_status (
    scooter_id Utf8,
    payload    String,
    update_ts  Uint64,
    PRIMARY KEY (scooter_id)
) WITH (
    TTL = Interval ("PT5M") ON update_ts AS MILLISECONDS
);