### Wireframe
![wireframe](docs/images/wireframe.png)

### Briefly
- Data need to be combined from multiple data sources.
- Pagination is required.
- Can filter by name of user, name of membership plan.
- Can sort by membership plan name, score.

### High level design
![HLD](docs/images/HLD.png)

#### Enrich submission flow
![enrich-flow](docs/images/stream-join-table.png)

#### Update enriched submission when user is updated flow
![update-flow](docs/images/update-enriched-submission.png)

### Kafka topics

| Topic Name                    | Description |
|------------                   |-------------|
| user-topic                    | User information|
| member-topic                  | Member in membership information |
| submission-topic              | User's submission information |
| enriched-submission-topic     | Enriched submission information |

