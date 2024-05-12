pub mod scooters {
    tonic::include_proto!("io.dmtri.scooters");
    tonic::include_proto!("io.dmtri.scooters.service");
}
pub mod strategy;
pub mod utils;

use std::time::{Duration, SystemTime, UNIX_EPOCH};

use envconfig::Envconfig;
use rand::{thread_rng, RngCore};
use scooters::scooters_api_client::*;
use scooters::*;
use tokio::time;
use tonic::transport::Channel;

use strategy::*;

type Client = ScootersApiClient<Channel>;
type AppResult<T> = Result<T, anyhow::Error>;

#[derive(Envconfig)]
struct Config {
    #[envconfig(from = "INTERVAL")]
    pub interval: f32,
    #[envconfig(from = "SERVER")]
    pub server: String,
}

#[tokio::main]
async fn main() -> AppResult<()> {
    let config = Config::init_from_env()?;

    let client = ScootersApiClient::connect(config.server).await?;

    let handle = tokio::spawn(client_loop(
        thread_rng().next_u64(),
        config.interval,
        ScooterData::default(),
        ScooterStrategy::RandomStrategy(RandomStrategy::default()),
        client.clone(),
    ));

    handle.await??;
    Ok(())
}

async fn client_loop(
    id: u64,
    time_delta: f32,
    mut scooter_data: ScooterData,
    mut scooter_strategy: ScooterStrategy,
    mut client: Client,
) -> AppResult<()> {
    let mut interval = time::interval(Duration::from_secs_f32(time_delta));

    loop {
        interval.tick().await;
        scooter_strategy.tick(&mut scooter_data, time_delta);
        let (coords, speed) = scooter_data.tick(time_delta);

        let request = tonic::Request::new(ScooterTelemetry {
            scooter_id: id.to_string(),
            timestamp: SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_millis() as u64,
            coordinates: Some(coords),
            speed: Some(speed),
        });
        let resp = client.send_telemetry(request).await?;
        scooter_data.slowdown(resp.get_ref().speed_limit);
    }
}
