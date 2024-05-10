fn main() -> Result<(), Box<dyn std::error::Error>> {
    tonic_build::compile_protos("../proto/model.proto")?;
    tonic_build::compile_protos("../proto/scooters_api.proto")?;
    Ok(())
}