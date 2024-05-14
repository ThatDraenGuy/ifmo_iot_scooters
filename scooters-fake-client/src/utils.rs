use rand::{thread_rng, Rng};

pub fn random(min: f32, max: f32) -> f32 {
    thread_rng().gen_range(min..=max)
}

pub fn random_signed(min: f32, max: f32) -> f32 {
    random(min, max)
        * if thread_rng().gen_bool(0.5) {
            1.0
        } else {
            -1.0
        }
}
