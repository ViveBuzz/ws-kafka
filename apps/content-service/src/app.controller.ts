import { Body, Controller, Get, Post } from '@nestjs/common';
import { AppService } from './app.service';
import { EventPattern, Payload } from '@nestjs/microservices';

@Controller('submissions')
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getSubmission(): any {
    return this.appService.getEnrichedSubmissions();
  }

  @Post()
  createSubmission(@Body() submission: any) {
    return this.appService.createSubmission(submission);
  }

  @EventPattern('enriched_submission_topic')
  async handleProcessPayment(@Payload() data: any) {
    console.log('Save enriched submission...');
    await this.appService.processEnrichedSubmission(data);
  }
}
