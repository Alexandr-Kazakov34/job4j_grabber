CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT INTO company (id, name) VALUES
(1, 'Company A'),
(2, 'Company B'),
(3, 'Company C'),
(4, 'Company D'),
(5, 'Company E');

INSERT INTO person (id, name, company_id) VALUES
(1, 'Alice', 1),
(2, 'Bob', 2),
(3, 'Charlie', 3),
(4, 'David', 4),
(5, 'Eve', 5),
(6, 'Frank', 1),
(7, 'Grace', 2),
(8, 'Heidi', 3),
(9, 'Ivan', 4),
(10, 'Judy', 1),
(11,'Gjon', 2);

--Task1:
SELECT
    person.name AS person_name,
    company.name AS company_name
FROM
    person
JOIN
    company ON person.company_id = company.id
WHERE
    person.company_id != 5;


--Task2:
SELECT
    company.name AS company_name,
    COUNT(person.id) AS person_count
FROM
    company
JOIN
    person ON company.id = person.company_id
GROUP BY
    company.id
HAVING
    COUNT(person.id) = (
        SELECT
            MAX(count)
        FROM (
            SELECT
                COUNT(person.id) AS count
            FROM
                person
            GROUP BY
                company_id
        ) AS max_count
    );