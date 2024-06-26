from math import acos, degrees
from random import random
from time import sleep
from turtle import Turtle, tracer, update

import grpc

from proto.map_model_pb2 import BoundingBox, SpeedLimitZone, GetSpeedLimitedZonesRequest, SpeedLimitedZones
from proto.model_pb2 import ScooterTelemetry, ScooterStatusesRequest, ScooterStatusesResponse
from proto.scooters_api_pb2_grpc import ScootersApiStub
from scooter_client.settings import ScooterSettings
from scooter_client.utils import speed


def to_turtle(x: float, y: float, scooter_settings: ScooterSettings) -> tuple[float, float]:
    return (
        x / (scooter_settings.map_x_upper - scooter_settings.map_x_lower) * 400,
        y / (scooter_settings.map_y_upper - scooter_settings.map_y_lower) * 400,
    )


def draw_box(t: Turtle, zone: SpeedLimitZone, scooter_settings: ScooterSettings):
    box = zone.box

    t.fillcolor(1, 69/255, 56/255)
    t.penup()
    t.goto(*to_turtle(box.top_left_corner.x, box.top_left_corner.y, scooter_settings))
    t.pendown()
    t.begin_fill()
    t.goto(*to_turtle(box.top_left_corner.x, box.bottom_right_corner.y, scooter_settings))
    t.goto(*to_turtle(box.bottom_right_corner.x, box.bottom_right_corner.y, scooter_settings))
    t.goto(*to_turtle(box.bottom_right_corner.x, box.top_left_corner.y, scooter_settings))
    t.goto(*to_turtle(box.top_left_corner.x, box.top_left_corner.y, scooter_settings))
    t.end_fill()
    t.penup()
    t.fillcolor(0, 0, 0)
    t.write(zone.speed_limit)

def draw_turtle(turtle: Turtle, turtle_status: ScooterTelemetry):
    (turtle_x, turtle_y) = to_turtle(
        turtle_status.coordinates.x, turtle_status.coordinates.y, settings
    )

    new_speed = speed(turtle_status.speed.x, turtle_status.speed.y)

    if new_speed != 0:
        sign = 1 if turtle_status.speed.y >= 0 else -1
        direction = (
                degrees(
                    acos(turtle_status.speed.x / speed(turtle_status.speed.x, turtle_status.speed.y))
                )
                * sign
        )
        turtle.setheading(direction)

    turtle.goto(turtle_x, turtle_y)


if __name__ == "__main__":
    settings = ScooterSettings()
    print(settings.model_dump_json())

    turtles: dict[str, tuple[Turtle, int]] = {}

    border_t = Turtle()
    border_t.hideturtle()
    border_t.speed(0)
    border_t.penup()
    border_t.goto(*to_turtle(settings.map_x_lower, settings.map_y_upper, settings))
    border_t.pendown()
    border_t.goto(*to_turtle(settings.map_x_lower, settings.map_y_lower, settings))
    border_t.goto(*to_turtle(settings.map_x_upper, settings.map_y_lower, settings))
    border_t.goto(*to_turtle(settings.map_x_upper, settings.map_y_upper, settings))
    border_t.goto(*to_turtle(settings.map_x_lower, settings.map_y_upper, settings))
    border_t.hideturtle()

    limit_zone_turtle = Turtle()
    limit_zone_turtle.hideturtle()

    last_map_version = 0

    tracer(0, 0)

    channel = grpc.insecure_channel(f"{settings.grpc_host}:{settings.grpc_port}")
    client = ScootersApiStub(channel)

    while True:
        response: ScooterStatusesResponse = client.getStatuses(ScooterStatusesRequest())
        statuses = response.statuses
        zones: SpeedLimitedZones = client.getSpeedLimitedZones(GetSpeedLimitedZonesRequest())

        if (zones.version != last_map_version):
            last_map_version = zones.version
            limit_zone_turtle.reset()
            limit_zone_turtle.hideturtle()
            for zone in zones.zones:
                draw_box(limit_zone_turtle, zone, settings)

        index: dict[str, ScooterTelemetry] = {status.scooter_id: status for status in statuses}

        for scooter_id, (t, lost_count) in turtles.items():
            if scooter_id not in index:
                if lost_count + 1 >= settings.lost_counter:
                    t.reset()
                    t.hideturtle()
                    del turtles[scooter_id]
                    print(f"Lost scooter: {scooter_id}")
                else:
                    turtles[scooter_id] = (t, lost_count + 1)
                continue

            status = index[scooter_id]
            draw_turtle(t, status)

        for status in statuses:
            if status.scooter_id in turtles:
                continue

            t = Turtle()
            t.speed(0)
            t.color(random(), random(), random())
            t.penup()
            draw_turtle(t, status)
            t.pendown()

            turtles[status.scooter_id] = (t, 0)

            print(f"New scooter: {status.scooter_id}")

        update()
        print("Finished round")
