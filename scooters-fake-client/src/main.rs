pub mod scooters {
    tonic::include_proto!("io.dmtri.scooters");
    tonic::include_proto!("io.dmtri.scooters.service");
}
pub mod strategy;
pub mod utils;

use std::time::{Duration, SystemTime, UNIX_EPOCH};

use rand::{thread_rng, RngCore};
use scooters::scooters_api_client::*;
use scooters::*;
use tokio::time;
use tonic::transport::Channel;

use strategy::*;

type Client = ScootersApiClient<Channel>;
type AppResult<T> = Result<T, anyhow::Error>;
type Vec2 = [f32; 2];

#[tokio::main]
async fn main() -> AppResult<()> {
    dotenvy::dotenv()?;

    let client = ScootersApiClient::connect(std::env::var("server")?).await?;

    let handle = tokio::spawn(client_loop(
        thread_rng().next_u64(),
        ScooterData::default(),
        ScooterStrategy::RandomStrategy(RandomStrategy {}),
        client.clone(),
    ));

    handle.await??;
    Ok(())
}

async fn client_loop(
    id: u64,
    mut scooter_data: ScooterData,
    mut scooter_strategy: ScooterStrategy,
    mut client: Client,
) -> AppResult<()> {
    let mut interval = time::interval(Duration::from_millis(500));

    loop {
        interval.tick().await;
        scooter_strategy.tick(&mut scooter_data);
        let (coords, speed) = scooter_data.tick();

        let request = tonic::Request::new(ScooterTelemetry {
            scooter_id: id.to_string(),
            timestamp: SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_millis() as u64,
            coordinates: Some(coords),
            speed: Some(speed),
        });
        client.send_telemetry(request).await?;
    }
}
