from math import acos, degrees
from random import random
from time import sleep
from turtle import Turtle

from scooter_client.scooter import Scooter
from scooter_client.settings import ScooterSettings
from scooter_client.utils import speed


def to_turtle(x: float, y: float, scooter_settings: ScooterSettings) -> tuple[float, float]:
    return (
        x / (scooter_settings.map_x_upper - scooter_settings.map_x_lower) * 400,
        y / (scooter_settings.map_y_upper - scooter_settings.map_y_lower) * 400,
    )


if __name__ == "__main__":
    settings = ScooterSettings()
    print(settings.model_dump_json())

    scooters: list[Scooter] = []
    turtles: list[Turtle] = []

    border_t = Turtle()
    border_t.speed(0)
    border_t.penup()
    border_t.goto(*to_turtle(settings.map_x_lower, settings.map_y_upper, settings))
    border_t.pendown()
    border_t.goto(*to_turtle(settings.map_x_lower, settings.map_y_lower, settings))
    border_t.goto(*to_turtle(settings.map_x_upper, settings.map_y_lower, settings))
    border_t.goto(*to_turtle(settings.map_x_upper, settings.map_y_upper, settings))
    border_t.goto(*to_turtle(settings.map_x_lower, settings.map_y_upper, settings))
    border_t.hideturtle()

    for i in range(settings.scooters_amount):
        scooter = Scooter(settings)
        t = Turtle()
        t.speed(0)
        t.color(random(), random(), random())
        scooters.append(scooter)
        turtles.append(t)

    while True:
        for scooter, t in zip(scooters, turtles):
            scooter.move()
            (t_x, t_y) = to_turtle(scooter.x, scooter.y, settings)

            SIGN = 1 if scooter.y_velocity >= 0 else -1
            direction = (
                degrees(acos(scooter.x_velocity / speed(scooter.x_velocity, scooter.y_velocity)))
                * SIGN
            )

            t.goto(t_x, t_y)
            t.setheading(direction)

            print(scooter.get_telemetry())
        sleep(0.01)
