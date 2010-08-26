-- phpMyAdmin SQL Dump
-- version 2.11.6
-- http://www.phpmyadmin.net
--
-- Host: pharos.l3s.uni-hannover.de
-- Generation Time: Jul 29, 2010 at 05:13 PM
-- Server version: 5.0.77
-- PHP Version: 5.2.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `liwa`
--
CREATE DATABASE `liwa` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `liwa`;

-- --------------------------------------------------------

--
-- Table structure for table `arc_conj`
--

DROP TABLE IF EXISTS `arc_conj`;
CREATE TABLE IF NOT EXISTS `arc_conj` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `value` varchar(20) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `new_index` (`id`,`value`),
  KEY `new_index2` (`value`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `arc_crawl`
--

DROP TABLE IF EXISTS `arc_crawl`;
CREATE TABLE IF NOT EXISTS `arc_crawl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `filename` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `new_index1` (`id`,`filename`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1  ;

-- --------------------------------------------------------

--
-- Table structure for table `arc_crawl_document`
--

DROP TABLE IF EXISTS `arc_crawl_document`;
CREATE TABLE IF NOT EXISTS `arc_crawl_document` (
  `id_crawl` int(11) default NULL,
  `id_document` int(11) default NULL,
  `date` date NOT NULL,
  KEY `id_document` (`id_document`),
  KEY `id_crawl` (`id_crawl`),
  KEY `new_index` (`id_document`,`date`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `arc_document`
--

DROP TABLE IF EXISTS `arc_document`;
CREATE TABLE IF NOT EXISTS `arc_document` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `url` varchar(500) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `new_index1` (`id`,`url`,`date`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 ;

-- --------------------------------------------------------

--
-- Table structure for table `arc_document_conj`
--

DROP TABLE IF EXISTS `arc_document_conj`;
CREATE TABLE IF NOT EXISTS `arc_document_conj` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `id_document` int(10) unsigned NOT NULL,
  `id_conj` int(10) unsigned NOT NULL,
  `begin` int(10) unsigned NOT NULL,
  `end` int(10) unsigned NOT NULL,
  `id_sentence` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `new_index` (`id`,`id_document`),
  KEY `sentence_index` (`id_sentence`),
  KEY `new_index2` (`begin`,`id_document`),
  KEY `new_index3` (`end`,`id_document`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 ;

-- --------------------------------------------------------

--
-- Table structure for table `arc_document_lemma`
--

DROP TABLE IF EXISTS `arc_document_lemma`;
CREATE TABLE IF NOT EXISTS `arc_document_lemma` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `id_document` int(10) unsigned NOT NULL,
  `id_lemma` int(10) unsigned default NULL,
  `begin` int(10) unsigned NOT NULL,
  `end` int(10) unsigned NOT NULL,
  `pos` varchar(50) NOT NULL,
  `id_sentence` int(10) unsigned NOT NULL,
  `lemma` varchar(256) default NULL,
  PRIMARY KEY  (`id`),
  KEY `new_index3` (`id_lemma`,`begin`),
  KEY `new_index6` (`id_document`,`id_lemma`,`begin`),
  KEY `new_index` (`id_document`,`id_lemma`,`begin`,`end`,`id_sentence`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1  ;

-- --------------------------------------------------------

--
-- Table structure for table `arc_document_sentence`
--

DROP TABLE IF EXISTS `arc_document_sentence`;
CREATE TABLE IF NOT EXISTS `arc_document_sentence` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `id_document` int(10) unsigned NOT NULL,
  `begin` int(10) unsigned NOT NULL,
  `end` int(10) unsigned NOT NULL,
  `text` text,
  PRIMARY KEY  (`id`),
  KEY `new_index2` (`id`,`id_document`,`begin`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1  ;

-- --------------------------------------------------------

--
-- Table structure for table `arc_graph`
--

DROP TABLE IF EXISTS `arc_graph`;
CREATE TABLE IF NOT EXISTS `arc_graph` (
  `id_lemma1` int(11) NOT NULL,
  `id_lemma2` int(11) NOT NULL,
  `count` int(11) NOT NULL,
  `date1` date NOT NULL,
  `date2` date NOT NULL,
  KEY `new_index` USING BTREE (`id_lemma1`,`id_lemma2`,`count`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `arc_lemma`
--

DROP TABLE IF EXISTS `arc_lemma`;
CREATE TABLE IF NOT EXISTS `arc_lemma` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `value` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unq` (`value`),
  KEY `new_index2` (`value`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1  ;
