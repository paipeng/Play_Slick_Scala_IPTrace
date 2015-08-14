# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `CAT` (`name` VARCHAR(254) NOT NULL PRIMARY KEY,`color` VARCHAR(254) NOT NULL);
create table `ip_addresses` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`token_id` BIGINT NOT NULL,`address` VARCHAR(254) NOT NULL,`created_at` timestamp default now() NOT NULL);
create table `ip_tokens` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`get_token` VARCHAR(254) NOT NULL,`set_token` VARCHAR(254) NOT NULL,`created_at` timestamp default now() NOT NULL);
alter table `ip_addresses` add constraint `fk_geo_location` foreign key(`token_id`) references `ip_tokens`(`id`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE ip_addresses DROP FOREIGN KEY fk_geo_location;
drop table `ip_tokens`;
drop table `ip_addresses`;
drop table `CAT`;

