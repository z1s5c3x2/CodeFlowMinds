drop database if exists codeflowminds;
create database codeflowminds;
use codeflowminds;
drop table if exists member;
create table member(
	mno int auto_increment,
    mid varchar(20),
	mpwd varchar(20),
	mcount int,
    mwincount int,
	primary key(mno)
);
drop table if exists record;
create table record(
	rno int auto_increment,
    mno int ,
    rcount int,
    rwincount int,
	primary key( rno ),
    foreign key( mno ) references member( mno )
);
select * from member;
select * from member order by mwincount desc limit 5;
select sum(rcount) from record where mno = 1;
select mno from member where mid = "naa1234";
insert into record( mno , rcount , rwindcount ) values( ? , ? , ?)