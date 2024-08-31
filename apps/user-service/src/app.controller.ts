import { Body, Controller, Get, Post } from '@nestjs/common';
import { AppService } from './app.service';
import { User } from '@ws/domains';

@Controller('users')
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Post()
  createUser(@Body() user: User): Promise<User> {
    return this.appService.createUser(user);
  }

  @Get()
  getUsers(): Promise<User[]> {
    return this.appService.getUsers();
  }
}
