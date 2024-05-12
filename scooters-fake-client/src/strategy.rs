use enum_dispatch::enum_dispatch;
use glam::Vec2;

use crate::utils::{random, random_signed};

use super::scooters::Vector;

pub struct ScooterData {
    border: f32,
    speed: Vec2,
    position: Vec2,
}
impl ScooterData {
    pub fn new(border: f32) -> Self {
        Self {
            border,
            speed: Vec2::default(),
            position: Vec2::default(),
        }
    }

    pub fn tick(&mut self, time_delta: f32) -> (Vector, Vector) {
        self.position += self.speed * time_delta;

        if self.position.x.abs() > self.border {
            self.speed.x *= -1.0;
            self.position.x = self.border * self.position.x.signum();
        }
        if self.position.y.abs() > self.border {
            self.speed.y *= -1.0;
            self.position.y = self.border * self.position.y.signum();
        }

        (self.position.into(), self.speed.into())
    }
    pub fn slowdown(&mut self, speed_limit: f32) {
        if self.speed.length() > speed_limit {
            self.speed *= speed_limit / self.speed.length();
        }
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

pub struct RandomStrategy {
    time_remaining: f32,
    min_speed: f32,
    max_speed: f32,
}
impl RandomStrategy {
    pub fn new(min_speed: f32, max_speed: f32) -> Self {
        Self {
            time_remaining: 0.0,
            min_speed,
            max_speed,
        }
    }
}
impl ScooterStrategyTrait for RandomStrategy {
    fn tick(&mut self, scooter_data: &mut ScooterData, time_delta: f32) {
        self.time_remaining -= time_delta;
        if self.time_remaining <= 0.0 {
            scooter_data.speed = Vec2::new(
                random_signed(self.min_speed, self.max_speed),
                random_signed(self.min_speed, self.max_speed),
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
