fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::configure().protoc_arg("-I..").compile(
        &[
            "../proto/model.proto",
            "../proto/scooters_api.proto",
            "../proto/map_model.proto",
        ],
        &[""],
    )?;
    Ok(())
}
