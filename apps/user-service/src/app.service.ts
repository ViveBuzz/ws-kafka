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

  async createUser(user: User): Promise<User> {
    const newUser = {
      id: nanoid(),
      ...user,
    };
    await this.userRepository.insert(newUser);
    this.clientKafka.emit('user_topic', {
      key: newUser.id,
      value: JSON.stringify(newUser),
    });
    return newUser;
  }

  async getUsers(): Promise<User[]> {
    return this.userRepository.find();
  }
}
