
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('sombrancelha','00:15:00',12,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Limpeza de pele','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('massagem','00:30:00',20,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('sombrancelha2','00:15:00',12,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Limpeza de pele2','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('massagem2','00:30:00',20,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('sombrancelha3','00:15:00',12,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Limpeza de pele3','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('massagem3','00:30:00',20,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('sombrancelha4','00:15:00',12,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Limpeza de pele4','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('massagem4','00:30:00',20,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('sombrancelha5','00:15:00',12,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Limpeza de pele5','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('massagem5','00:30:00',20,'muito bom',true);
INSERT INTO usuario (aceito, ativo, email, nome, senha, telefone) VALUES ( false, true, 'adm1@adm.com','adm1', 123, '(99) 99999-9999');
INSERT INTO usuario (aceito, ativo, email, nome, senha, telefone) VALUES ( false, true, 'adm2@adm.com','adm2', 123, '(99) 99999-9999');
INSERT INTO usuario (aceito, ativo, email, nome, senha, telefone) VALUES ( true, true, 'adm3@adm.com','adm3', 123, '(99) 99999-9999');
INSERT INTO usuario (aceito, ativo, email, nome, senha, telefone) VALUES ( true, true, 'adm4@adm.com','adm4', 123, '(99) 99999-9999');
INSERT INTO usuario (aceito, ativo, email, nome, senha, telefone) VALUES ( true, true, 'adm5@adm.com','adm5', 123, '(99) 99999-9999');
INSERT INTO usuario (aceito, ativo, email, nome, senha, telefone) VALUES ( true, true, 'adm6@adm.com','adm6', 123, '(99) 99999-9999');
INSERT INTO permissao(id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO permissao(id, nome) VALUES (2, 'ROLE_FUNCIONARIO');
INSERT INTO permissao(id, nome) VALUES (3, 'ROLE_ATENDENTE');
INSERT INTO permissao(id, nome) VALUES (4, 'ROLE_CLIENTE');
INSERT INTO permissao(id, nome) VALUES (5, 'ROLE_CADASTRADO');
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (4, 1);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (4, 2);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (2, 2);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (3, 3);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (4, 4);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (5, 5);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (5, 6);