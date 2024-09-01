import { Column, Entity } from 'typeorm';
import { IdentityEntity } from './base.entity';

@Entity()
export class EnrichedSubmission extends IdentityEntity {
  constructor(partial: Partial<EnrichedSubmission>) {
    super();
    Object.assign(this, partial);
  }

  @Column({ type: 'varchar', length: 50 })
  status!: string;

  @Column({ type: 'datetime' })
  date!: Date;

  @Column({ type: 'json' })
  user!: any;

  @Column({ type: 'json' })
  membership!: any;
}
