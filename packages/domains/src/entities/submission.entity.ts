import { Column, Entity } from 'typeorm';
import { IdentityEntity } from './base.entity';

@Entity()
export class Submission extends IdentityEntity {
  constructor(partial: Partial<Submission>) {
    super();
    Object.assign(this, partial);
  }

  @Column({ type: 'varchar', length: 50 })
  status!: string;

  @Column({ type: 'datetime' })
  date!: Date;

  @Column({ type: 'varchar', length: 50 })
  userId!: string;
}
