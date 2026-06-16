CREATE TABLE IF NOT EXISTS contatos
(
    id       SERIAL PRIMARY KEY,
    nome     VARCHAR(60) NOT NULL UNIQUE,
    email    VARCHAR(60) NOT NULL UNIQUE,
    telefone VARCHAR(11) NOT NULL
);

BEGIN;

INSERT INTO contatos (nome, email, telefone)
VALUES ('Ana Silva', 'ana.silva@example.com', '11987654321'),
       ('Bruno Almeida', 'bruno.almeida@example.com', '21991234567'),
       ('Carla Souza', 'carla.souza@example.com', '31999887766'),
       ('Diego Pereira', 'diego.pereira@example.com', '41991223344'),
       ('Eduarda Martins', 'eduarda.martins@example.com', '51991122334'),
       ('Felipe Costa', 'felipe.costa@example.com', '61992334455'),
       ('Gabriela Rocha', 'gabriela.rocha@example.com', '71993445566'),
       ('Hugo Fernandes', 'hugo.fernandes@example.com', '81994556677'),
       ('Isabela Lima', 'isabela.lima@example.com', '91995667788'),
       ('João Carvalho', 'joao.carvalho@example.com', '11996778899');

COMMIT;

-- Notas:
-- - Os telefones estão no formato 11 dígitos (DDD + número) sem separadores, conforme a restrição VARCHAR(11).
-- - Os nomes e e-mails são únicos para respeitar as constraints da tabela.

