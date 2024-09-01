import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import { ClientKafka } from '@nestjs/microservices';
import { InjectRepository } from '@nestjs/typeorm';
import { Submission } from '@ws/domains';
import { nanoid } from 'nanoid';
import { Repository } from 'typeorm';

@Injectable()
export class AppService implements OnModuleInit {
  constructor(
    @InjectRepository(Submission)
    private readonly submissionRepository: Repository<Submission>,
    @Inject('content_service') private readonly clientKafka: ClientKafka,
  ) {}
  getHello(): string {
    return 'Hello World!';
  }

  async createSubmission(submission: Submission): Promise<Submission> {
    const newSubmission = {
      id: nanoid(),
      status: 'pending',
      date: new Date(),
      ...submission,
    };
    await this.submissionRepository.insert(newSubmission);
    this.clientKafka.emit('submission_topic', JSON.stringify(newSubmission));
    return newSubmission;
  }

  processEnrichedSubmission(submission: any) {
    console.log(submission);
  }

  onModuleInit() {
    this.clientKafka.subscribeToResponseOf('enriched_submission_topic');
  }
}
