import { Module } from '@nestjs/common';
import { EnrichedSubmission, Submission } from '@ws/domains';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ClientsModule, Transport } from '@nestjs/microservices';

@Module({
  imports: [
    TypeOrmModule.forFeature([Submission, EnrichedSubmission]),
    TypeOrmModule.forRoot({
      type: 'mysql',
      host: '127.0.0.1',
      port: 3306,
      username: 'root',
      password: '',
      database: 'ws',
      entities: [],
      synchronize: true,
      autoLoadEntities: true,
    }),
    ClientsModule.register([
      {
        name: 'content_service',
        transport: Transport.KAFKA,
        options: {
          client: {
            clientId: 'content',
            brokers: ['localhost:29092'],
          },
          consumer: {
            groupId: 'content-consumer',
          },
        },
      },
    ]),
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
