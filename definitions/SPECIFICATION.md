# Specification

The specification of a definition of an entity. Every definition must have a `entities` property, in which
is an array of entity definitions.

## Properties

* `name` (string): The name of the entity.
* `location` (object):
* * `rust` (string): The location of the entity in the Rust source code.
* * `kotlin` (string): The location of the entity in the Kotlin source code.
* `feature_flag` (string, only available in Rust) The crate feature flag that must be enabled to use this entity.
* `fields` (object)

## Fields

A field is a property of an entity structure which can be any type of data. The fields must have the following
properties:

* `type` (object):
* * `rust` (string): The type of the field in the Rust source code.
* * `kotlin` (string): The type of the field in the Kotlin source code.
* `sql` (string | optional): Attached for the column in database code.

## Examples

**player.yml**
```yml
---
entities:
  - name: Player
    package: org.hexalite.generation.entity
    fields:
       uuid:
         kotlin: java.util.UUID
         rust: uuid::Uuid
       hexes:
         kotlin: Int
         rust: u32
       created_at:
         kotlin: kotlinx.datetime.Instant
         rust: chrono::DateTime<chrono::Utc>
