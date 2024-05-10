use enum_dispatch::enum_dispatch;

use crate::{utils::random, Vec2};

use super::scooters::Vector;

#[derive(Default)]
pub struct ScooterData {
    speed: Vec2,
    position: Vec2,
}
impl ScooterData {
    pub fn tick(&mut self) -> (Vector, Vector) {
        vec2::add_mut(&mut self.position, &self.speed);

        (self.position.into(), self.speed.into())
    }
}

#[enum_dispatch]
pub trait ScooterStrategyTrait {
    fn init(&mut self, _scooter_data: &mut ScooterData) {}
    fn tick(&mut self, scooter_data: &mut ScooterData);
}

#[enum_dispatch(ScooterStrategyTrait)]
pub enum ScooterStrategy {
    RandomStrategy,
}

pub struct RandomStrategy;
impl RandomStrategy {
    const MIN: f32 = 0.0;
    const MAX: f32 = 8.33;
}
impl ScooterStrategyTrait for RandomStrategy {
    fn tick(&mut self, scooter_data: &mut ScooterData) {
        scooter_data.speed = [random(Self::MIN, Self::MAX), random(Self::MIN, Self::MAX)]
    }
}

// pub struct GoalStrategy {
//     target: Vec2,
// }
// impl ScooterStrategy for GoalStrategy {
//     fn tick(&mut self, scooter_data: &mut ScooterData) {

//     }
// }

impl From<[f32; 2]> for Vector {
    fn from(value: [f32; 2]) -> Self {
        Vector {
            x: value[0],
            y: value[1],
        }
    }
}
