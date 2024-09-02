import { Inject, Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { User } from '@ws/domains';
import { InjectRepository } from '@nestjs/typeorm';
import { nanoid } from 'nanoid';
import { ClientKafka } from '@nestjs/microservices';

@Injectable()
export class AppService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
    @Inject('user_service') private readonly clientKafka: ClientKafka,
  ) {}

  async createUser(input: User): Promise<User> {
    const user = {
      id: input.id || nanoid(),
      ...input,
    };
    await this.userRepository.save(user);
    this.clientKafka.emit('user_topic', {
      key: user.id,
      value: JSON.stringify(user),
    });

    if (input.id) {
      this.clientKafka.emit('user_updated_topic', {
        key: user.id,
        value: JSON.stringify(user),
      });
    }
    return user;
  }

  async getUsers(): Promise<User[]> {
    return this.userRepository.find();
  }
}
