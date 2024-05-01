from random import choice, random
from string import hexdigits
from time import time

from proto.model_pb2 import ScooterTelemetry, Vector
from scooter_client.settings import ScooterSettings
from scooter_client.utils import speed


class Scooter:
    def __init__(self, settings: ScooterSettings):
        self.settings = settings
        self.id = "".join([choice(hexdigits) for i in range(10)])

        self.x = (
            random() * (self.settings.map_x_upper - self.settings.map_x_lower)
            + self.settings.map_x_lower
        )
        self.y = (
            random() * (self.settings.map_y_upper - self.settings.map_y_lower)
            + self.settings.map_y_lower
        )

        self.x_velocity = 0
        self.y_velocity = 0

    def move(self):
        x_acc = (random() * 2 - 1) * self.settings.acceleration_range
        y_acc = (random() * 2 - 1) * self.settings.acceleration_range
        new_x_velocity = self.x_velocity + x_acc
        new_y_velocity = self.y_velocity + y_acc

        new_speed = speed(new_x_velocity, new_y_velocity)
        new_corrected_speed = min(self.settings.max_speed, new_speed)
        correction_coefficient = new_corrected_speed / new_speed

        new_x_velocity = new_x_velocity * correction_coefficient
        new_y_velocity = new_y_velocity * correction_coefficient

        new_x = self.x + new_x_velocity
        new_y = self.y + new_y_velocity

        if new_x >= self.settings.map_x_upper:
            new_x = self.settings.map_x_upper
            new_x_velocity *= -1

        if new_x <= self.settings.map_x_lower:
            new_x = self.settings.map_x_lower
            new_x_velocity *= -1

        if new_y >= self.settings.map_y_upper:
            new_y = self.settings.map_y_upper
            new_y_velocity *= -1

        if new_y <= self.settings.map_y_lower:
            new_y = self.settings.map_y_lower
            new_y_velocity *= -1

        self.x = new_x
        self.y = new_y
        self.x_velocity = new_x_velocity
        self.y_velocity = new_y_velocity

    def get_telemetry(self) -> ScooterTelemetry:
        return ScooterTelemetry(
            scooter_id=self.id,
            timestamp=int(time()),
            coordinates=Vector(x=self.x, y=self.y),
            speed=Vector(x=self.x_velocity, y=self.y_velocity),
        )
