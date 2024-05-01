from pydantic_settings import BaseSettings, SettingsConfigDict


class ScooterSettings(BaseSettings):
    model_config = SettingsConfigDict(env_file=[".env", ".env.secrets"])

    map_x_lower: int
    map_x_upper: int
    map_y_lower: int
    map_y_upper: int
    max_speed: float
    acceleration_range: float
    secret: str
    scooters_amount: int
