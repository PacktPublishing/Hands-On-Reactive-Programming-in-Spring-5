# Comparison of naive reactive implementation and transactional reactive implementation.

Set up was tested with MacOS.

Requirements:

* Docker

## Naive implementation

Uses no transactional consistency and consequently produces incorrect final state. 

## Transactional reactive implementation

Uses transactional consistency and produces correct final state.
Uses retry strategy when transaction fails due to conflicting transactions.  