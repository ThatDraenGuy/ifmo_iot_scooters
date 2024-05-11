use enum_dispatch::enum_dispatch;
use glam::Vec2;

use crate::utils::random;

use super::scooters::Vector;

#[derive(Default)]
pub struct ScooterData {
    speed: Vec2,
    position: Vec2,
}
impl ScooterData {
    pub fn tick(&mut self, time_delta: f32) -> (Vector, Vector) {
        self.position += self.speed * time_delta;

        (self.position.into(), self.speed.into())
    }
}

#[enum_dispatch]
pub trait ScooterStrategyTrait {
    fn init(&mut self, _scooter_data: &mut ScooterData) {}
    fn tick(&mut self, scooter_data: &mut ScooterData, time_delta: f32);
}

#[enum_dispatch(ScooterStrategyTrait)]
pub enum ScooterStrategy {
    RandomStrategy,
}

#[derive(Default)]
pub struct RandomStrategy {
    time_remaining: f32,
}
impl RandomStrategy {
    const MIN_AXIS_SPEED: f32 = 0.0;
    const MAX_AXIS_SPEED: f32 = 8.33;
}
impl ScooterStrategyTrait for RandomStrategy {
    fn tick(&mut self, scooter_data: &mut ScooterData, time_delta: f32) {
        self.time_remaining -= time_delta;
        if self.time_remaining <= 0.0 {
            scooter_data.speed = Vec2::new(
                random(Self::MIN_AXIS_SPEED, Self::MAX_AXIS_SPEED),
                random(Self::MIN_AXIS_SPEED, Self::MAX_AXIS_SPEED),
            );
            self.time_remaining = random(5.0, 20.0);
        }
    }
}

impl From<Vec2> for Vector {
    fn from(value: Vec2) -> Self {
        Vector {
            x: value[0],
            y: value[1],
        }
    }
}
