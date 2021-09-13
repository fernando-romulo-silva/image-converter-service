
DELETE FROM IMAGE_CONVERTION;

DELETE FROM IMAGE_TYPE;

insert into image_type (imt_id, imt_extension, imt_name) values (1000, 'png', 'Portable Network Graphics');
            
insert into image_type (imt_id, imt_extension, imt_name) values (1001, 'jpg', 'Joint Photographics Experts Group');
            
insert into IMAGE_CONVERTION (IMGC_ID, IMGC_NAME, IMT_ID, IMGC_SIZE, IMGC_DATE, IMGC_TYPE, IMGC_TEXT) values (1000, 'image_test.jpg', 1001, 4000, CURRENT_TIMESTAMP, 'BATCH', '02325678908110000003556752101015176230000023560');