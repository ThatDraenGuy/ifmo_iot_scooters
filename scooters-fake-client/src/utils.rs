use rand::{thread_rng, Rng};

pub fn random(min: f32, max: f32) -> f32 {
    thread_rng().gen_range(min..max)
}
