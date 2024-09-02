import { Inject, Injectable, OnModuleInit } from '@nestjs/common';
import { ClientKafka } from '@nestjs/microservices';
import { InjectRepository } from '@nestjs/typeorm';
import { EnrichedSubmission, Submission } from '@ws/domains';
import { nanoid } from 'nanoid';
import { Repository } from 'typeorm';

@Injectable()
export class AppService implements OnModuleInit {
  constructor(
    @InjectRepository(Submission)
    private readonly submissionRepository: Repository<Submission>,
    @InjectRepository(EnrichedSubmission)
    private readonly enrichedSubmissionRepository: Repository<EnrichedSubmission>,
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

  async processEnrichedSubmission(submission: any) {
    await this.enrichedSubmissionRepository.save({
      ...submission,
      user: submission.user || {},
      membership: submission.membership || {},
    });
  }

  async getEnrichedSubmissions(): Promise<any> {
    const data = await this.enrichedSubmissionRepository.find({
      order: { date: 'DESC' },
      take: 10,
    });
    return { data };
  }

  onModuleInit() {
    this.clientKafka.subscribeToResponseOf('enriched_submission_topic');
  }
}
