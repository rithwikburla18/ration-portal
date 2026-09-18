--
-- PostgreSQL database dump
--

\restrict PkgBmPmnYCu3s0W1dZjUzqfGA2SrK1CDRnZQtqCZpTO0tKjiV6fYXnGUDJVsi8T

-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: distribution_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.distribution_logs (
    id bigint NOT NULL,
    ration_card_id bigint,
    member_id bigint,
    quantity_kg numeric(10,2) NOT NULL,
    distribution_date date NOT NULL,
    fps_id character varying(50),
    transaction_status character varying(30) NOT NULL
);


--
-- Name: distribution_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.distribution_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: distribution_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.distribution_logs_id_seq OWNED BY public.distribution_logs.id;


--
-- Name: family_members; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.family_members (
    id bigint NOT NULL,
    ration_card_id bigint NOT NULL,
    member_id character varying(50) NOT NULL,
    name character varying(120) NOT NULL,
    age integer NOT NULL,
    gender character varying(20),
    relationship character varying(60),
    rice_quota_kg numeric(10,2) DEFAULT 0 NOT NULL,
    CONSTRAINT family_members_age_check CHECK ((age >= 0))
);


--
-- Name: family_members_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.family_members_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: family_members_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.family_members_id_seq OWNED BY public.family_members.id;


--
-- Name: grievances; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.grievances (
    id bigint NOT NULL,
    grievance_number character varying(30) NOT NULL,
    applicant_name character varying(120) NOT NULL,
    ration_card_number character varying(50) NOT NULL,
    category character varying(50) NOT NULL,
    contact_number character varying(20) NOT NULL,
    description text NOT NULL,
    status character varying(30) DEFAULT 'SUBMITTED'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: grievances_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.grievances_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: grievances_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.grievances_id_seq OWNED BY public.grievances.id;


--
-- Name: ration_card_applications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ration_card_applications (
    id bigint NOT NULL,
    applicant_name character varying(120) NOT NULL,
    family_members integer NOT NULL,
    district character varying(100) NOT NULL,
    address text NOT NULL,
    status character varying(30) DEFAULT 'SUBMITTED'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ration_card_applications_family_members_check CHECK ((family_members >= 1))
);


--
-- Name: ration_card_applications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ration_card_applications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ration_card_applications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ration_card_applications_id_seq OWNED BY public.ration_card_applications.id;


--
-- Name: ration_cards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ration_cards (
    id bigint NOT NULL,
    ration_card_number character varying(50) NOT NULL,
    card_type character varying(20) NOT NULL,
    head_of_family character varying(120),
    address text,
    district character varying(100),
    state character varying(100),
    status character varying(30) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: ration_cards_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ration_cards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ration_cards_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.ration_cards_id_seq OWNED BY public.ration_cards.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    full_name character varying(120) NOT NULL,
    email character varying(150) NOT NULL,
    password_hash character varying(255) NOT NULL,
    role character varying(30) DEFAULT 'CITIZEN'::character varying NOT NULL,
    status character varying(30) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: distribution_logs id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.distribution_logs ALTER COLUMN id SET DEFAULT nextval('public.distribution_logs_id_seq'::regclass);


--
-- Name: family_members id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.family_members ALTER COLUMN id SET DEFAULT nextval('public.family_members_id_seq'::regclass);


--
-- Name: grievances id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grievances ALTER COLUMN id SET DEFAULT nextval('public.grievances_id_seq'::regclass);


--
-- Name: ration_card_applications id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ration_card_applications ALTER COLUMN id SET DEFAULT nextval('public.ration_card_applications_id_seq'::regclass);


--
-- Name: ration_cards id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ration_cards ALTER COLUMN id SET DEFAULT nextval('public.ration_cards_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: distribution_logs distribution_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.distribution_logs
    ADD CONSTRAINT distribution_logs_pkey PRIMARY KEY (id);


--
-- Name: family_members family_members_member_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.family_members
    ADD CONSTRAINT family_members_member_id_key UNIQUE (member_id);


--
-- Name: family_members family_members_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.family_members
    ADD CONSTRAINT family_members_pkey PRIMARY KEY (id);


--
-- Name: grievances grievances_grievance_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grievances
    ADD CONSTRAINT grievances_grievance_number_key UNIQUE (grievance_number);


--
-- Name: grievances grievances_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.grievances
    ADD CONSTRAINT grievances_pkey PRIMARY KEY (id);


--
-- Name: ration_card_applications ration_card_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ration_card_applications
    ADD CONSTRAINT ration_card_applications_pkey PRIMARY KEY (id);


--
-- Name: ration_cards ration_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ration_cards
    ADD CONSTRAINT ration_cards_pkey PRIMARY KEY (id);


--
-- Name: ration_cards ration_cards_ration_card_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ration_cards
    ADD CONSTRAINT ration_cards_ration_card_number_key UNIQUE (ration_card_number);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: distribution_logs distribution_logs_member_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.distribution_logs
    ADD CONSTRAINT distribution_logs_member_id_fkey FOREIGN KEY (member_id) REFERENCES public.family_members(id);


--
-- Name: distribution_logs distribution_logs_ration_card_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.distribution_logs
    ADD CONSTRAINT distribution_logs_ration_card_id_fkey FOREIGN KEY (ration_card_id) REFERENCES public.ration_cards(id);


--
-- Name: family_members family_members_ration_card_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.family_members
    ADD CONSTRAINT family_members_ration_card_id_fkey FOREIGN KEY (ration_card_id) REFERENCES public.ration_cards(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict PkgBmPmnYCu3s0W1dZjUzqfGA2SrK1CDRnZQtqCZpTO0tKjiV6fYXnGUDJVsi8T

