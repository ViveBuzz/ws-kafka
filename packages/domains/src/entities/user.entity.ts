import { Column, Entity } from 'typeorm';
import { IdentityEntity } from './base.entity';

@Entity()
export class User extends IdentityEntity {
  constructor(partial: Partial<User>) {
    super();
    Object.assign(this, partial);
  }

  @Column({ type: 'varchar', length: 50 })
  firstName!: string;

  @Column({ type: 'varchar', length: 50, nullable: true })
  lastName!: string;
}
