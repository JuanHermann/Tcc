
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Bloqueio','00:15:00',0,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Limpeza de pele','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Massagem','00:30:00',20,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Sobrancelha','00:15:00',12,'muito bom',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Microagulhamento','01:00:00',50,'',true);
INSERT INTO servico (nome,tempo,valor,descricao,ativo) VALUES ('Peeling de Diamante','00:30:00',100,'muito bom',true);
INSERT INTO usuario ( ativo, email, nome, senha, telefone) VALUES (  true, 'julio@adm.com','Julio Roberto da Fonseca', 123, '(99) 99123-9119');
INSERT INTO usuario ( ativo, email, nome, senha, telefone) VALUES (  true, 'maria@adm.com','Maria da Rosa', 123, '(99) 99312-9129');
INSERT INTO usuario ( ativo, email, nome, senha, telefone) VALUES (  true, 'debora@adm.com','Debora Almeida', 123, '(99) 99333-6798');
INSERT INTO usuario ( ativo, email, nome, senha, telefone) VALUES (  true, 'roberto@adm.com','Gustavo da Silva', 123, '(99) 99999-9999');
INSERT INTO usuario ( ativo, email, nome, senha, telefone) VALUES (  true, 'lucas@adm.com','Lucas Ferrari', 123, '(99) 99999-9999');
INSERT INTO usuario ( ativo, email, nome, senha, telefone) VALUES (  true, 'simone@adm.com','Simone Leticia Fernandes', 123, '(99) 99999-9999');
INSERT INTO permissao(id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO permissao(id, nome) VALUES (2, 'ROLE_FUNCIONARIO');
INSERT INTO permissao(id, nome) VALUES (3, 'ROLE_ATENDENTE');
INSERT INTO permissao(id, nome) VALUES (4, 'ROLE_CLIENTE');
INSERT INTO permissao(id, nome) VALUES (5, 'ROLE_CADASTRADO');
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (4, 1);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (4, 2);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (3, 3);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (4, 4);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (5, 5);
INSERT INTO usuario_permissao(permissao_id, usuario_id) VALUES (5, 6);