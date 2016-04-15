package hol2eih4;

public class SqlHolder {

	public static String icd10K1 = ""
			+" SELECT ICD_CODE, \n"
			+"        count(ICD_CODE), \n"
			+"        icd_name \n"
			+" FROM dity1.ix ix, \n"
			+"      list.icd icd \n"
			+" WHERE ix.ICD_CODE1=ICD_CODE \n"
			+" AND 1 = :min_month \n"
			+" GROUP BY ICD_CODE \n"
			+" ORDER BY ICD_CODE \n"
			+"  \n"
;
	public static String bedDayMySql = ""
			+" SELECT d.*, \n"
			+"        round(if(d.dead IS NULL, 0, d.dead/TREAT*100),2) mortality, \n"
			+"        round(bed_day * 100 / bed_day_plan,2) bed_day_fulfil, \n"
			+"        round(bed_day  / department_bed,2)  bed_occupancy, \n"
			+"        round(bed_day  / TREAT,1) treat_avg, \n"
			+"        round((bed_day  / department_bed)/( bed_day / TREAT ),2)  bed_turnover, \n"
			+"         round((d.in_clinic+ifnull(d.dead,0))/d.department_bed,2)  bed_turnover2 \n"
			+" FROM \n"
			+"   (SELECT d.*, \n"
			+"           round((d.in_clinic+ifnull(d.in_dep,0)+d.out_clinic+ifnull(d.out_dep,0)+ifnull(d.dead,0))/2) TREAT \n"
			+"    FROM \n"
			+"      (SELECT d.department_id dp_id, \n"
			+"              d.department_name, \n"
			+"              d.department_bed, \n"
			+"              if(d.department_id=14,310,340) bd_key, \n"
			+"              bed_day_plan, \n"
			+"              in_clinic.in_clinic, \n"
			+"              in_dep.in_dep, \n"
			+"              out_clinic.out_clinic, \n"
			+"              out_dep.out_dep, \n"
			+"              out_to_clinic.out_to_clinic, \n"
			+"              dead.dead, \n"
			+"              bed_day.bed_day \n"
			+"       FROM (-- відділення + розгорнуті ліжка + план ліжкоднів \n"
			+"             SELECT department_id, \n"
			+"                    department_name, \n"
			+"                    department_bed, \n"
			+"                    sum(bed_day_plan) bed_day_plan \n"
			+"             FROM (-- bed_day_plan \n"
			+"                   SELECT *, \n"
			+"                          round(bd_key/366*month_days*department_bed) bed_day_plan \n"
			+"                   FROM (-- відділення з ліжкоднями \n"
			+"                         SELECT d.department_id, \n"
			+"        department_bed, \n"
			+"        d.department_name, \n"
			+"        if(d.department_id=14,310,if(d.department_id=23 or d.department_id=10,290,340)) bd_key \n"
			+" FROM department_bed db1, \n"
			+"      department d, \n"
			+"   (SELECT max(department_bed_id) department_bed_id \n"
			+"    FROM department_bed \n"
			+"    GROUP BY department_id) db2 \n"
			+" WHERE db1.department_bed_id=db2.department_bed_id \n"
			+"   AND d.department_id=db1.department_id) department_bed, \n"
			+"                        (-- днів в місяці \n"
			+"                         SELECT max(day(daydate)) month_days \n"
			+"                         FROM every_day \n"
			+"                         WHERE month(daydate)  >= :min_month  \n"
			+"                           AND month(daydate)  <= :max_month  \n"
			+"                           AND year(daydate) = 2016 \n"
			+"                         GROUP BY month(daydate)) month_days) bed_day_plan \n"
			+"             GROUP BY department_id ) d \n"
			+"       LEFT JOIN (-- поступивші в лікарню \n"
			+"                  SELECT history_department_in, \n"
			+"                         count(history_department_in) in_clinic \n"
			+"                  FROM history \n"
			+"                  WHERE month(history_in)  >= :min_month  \n"
			+"                    AND month(history_in)  <= :max_month  \n"
			+"                    AND year(history_in)=2016 \n"
			+"                  GROUP BY history_department_in) in_clinic ON in_clinic.history_department_in=d.department_id \n"
			+"       LEFT JOIN (-- переведені з інши відділення \n"
			+"                  SELECT dh.department_id, \n"
			+"                         count(dh.department_id) in_dep \n"
			+"                  FROM \n"
			+"                    (SELECT dh.* \n"
			+"                     FROM department_history dh, \n"
			+"                          history h \n"
			+"                     WHERE h.history_id=dh.history_id \n"
			+"                       AND h.history_department_in != dh.department_id) dh \n"
			+"                  WHERE month(dh.department_history_in)  >= :min_month  \n"
			+"                    AND month(dh.department_history_in)  <= :max_month  \n"
			+"                    AND year(dh.department_history_in)=2016 \n"
			+"                  GROUP BY dh.department_id) in_dep ON in_dep.department_id=d.department_id \n"
			+"       LEFT JOIN (-- виписані з лікарні \n"
			+"                  SELECT history_department_out, \n"
			+"                         count(history_department_out) out_clinic \n"
			+"                  FROM history \n"
			+"                  WHERE month(history_out) >= :min_month  \n"
			+"                    AND month(history_out) <= :max_month  \n"
			+"                    AND year(history_out)=2016 \n"
			+"                  GROUP BY history_department_out) out_clinic ON out_clinic.history_department_out=d.department_id \n"
			+"       LEFT JOIN (-- переведені в інші відділення \n"
			+"                  SELECT dh.department_id, \n"
			+"                         count(dh.department_id) out_dep \n"
			+"                  FROM \n"
			+"                    (SELECT dh.* \n"
			+"                     FROM department_history dh, \n"
			+"                          history h \n"
			+"                     WHERE h.history_id=dh.history_id \n"
			+"                       AND h.history_department_out != dh.department_id) dh \n"
			+"                  WHERE month(dh.department_history_out) >= :min_month  \n"
			+"                    AND month(dh.department_history_out) <= :max_month  \n"
			+"                    AND year(dh.department_history_out)=2016 \n"
			+"                  GROUP BY dh.department_id) out_dep ON out_dep.department_id=d.department_id \n"
			+"       LEFT JOIN (-- переведені в іншу лікарню \n"
			+"                  SELECT history_department_out, \n"
			+"                         count(history_department_out) out_to_clinic \n"
			+"                  FROM history h \n"
			+"                  WHERE result_id=6 \n"
			+"                    AND month(history_out) >= :min_month  \n"
			+"                    AND month(history_out) <= :max_month  \n"
			+"                    AND year(history_out)=2016 \n"
			+"                  GROUP BY history_department_out) out_to_clinic ON out_to_clinic.history_department_out=d.department_id \n"
			+"       LEFT JOIN (-- померло \n"
			+"                  SELECT history_department_out, \n"
			+"                         count(history_department_out) dead \n"
			+"                  FROM history \n"
			+"                  WHERE month(history_out) >= :min_month  \n"
			+"                    AND month(history_out) <= :max_month  \n"
			+"                    AND year(history_out)=2016 \n"
			+"                    AND result_id=5 \n"
			+"                  GROUP BY history_department_out) dead ON dead.history_department_out=d.department_id \n"
			+"       LEFT JOIN (-- ліжкодні (сумма лікувались кожного дня) \n"
			+"                  SELECT department_id, \n"
			+"                         sum(TREAT) bed_day \n"
			+"                  FROM (-- лікувальсь в день \n"
			+"                        SELECT department_id, \n"
			+"                               daydate, \n"
			+"                               count(daydate) TREAT \n"
			+"                        FROM department_history dh, \n"
			+"                          (SELECT * \n"
			+"                           FROM every_day \n"
			+"                           WHERE month(daydate)  >= :min_month  \n"
			+"                             AND month(daydate)  <= :max_month  \n"
			+"                             AND year(daydate) = 2016) daydate \n"
			+"                        WHERE daydate>=dh.department_history_in \n"
			+"                          AND daydate<=dh.department_history_out \n"
			+"                        GROUP BY department_id, \n"
			+"                                 daydate) treat_of_day \n"
			+"                  GROUP BY department_id) bed_day ON bed_day.department_id=d.department_id) d)d \n"
			+" UNION \n"
			+" SELECT * \n"
			+" FROM \n"
			+"   (SELECT 0 dp_id, \n"
			+"           'Всього:' department_name, \n"
			+"                     0 d_bed, \n"
			+"                     0 d_bed_plan, \n"
			+"                     0 max_moth_day ) all_, \n"
			+"   (SELECT count(history_department_in) _in \n"
			+"    FROM history \n"
			+"    WHERE month(history_in) >= :min_month  \n"
			+"      AND month(history_in) <= :max_month  \n"
			+"      AND year(history_in)=2016) in_, \n"
			+"   (SELECT 0 in_dep) in_dep, \n"
			+"   (SELECT count(history_department_out) _out \n"
			+"    FROM history \n"
			+"    WHERE month(history_out) >= :min_month  \n"
			+"      AND month(history_out) <= :max_month  \n"
			+"      AND year(history_out)=2016) out_, \n"
			+"   (SELECT 0 out_dep) out_dep, \n"
			+"   (SELECT 0 out_to_clinic) out_to_clinic, \n"
			+"   (SELECT count(history_department_out) dead, \n"
			+"      (SELECT 0 l) bed_day, \n"
			+"      (SELECT 0 l) TREAT, \n"
			+"      (SELECT 0 l) mortality, \n"
			+"      0 bed_day_fulfil, \n"
			+" 0     bed_occupancy, \n"
			+" 0     treat_avg, \n"
			+" 0     bed_turnover, \n"
			+" 0     bed_turnover2 \n"
			+"    FROM history \n"
			+"    WHERE month(history_out) >= :min_month  \n"
			+"      AND month(history_out) <= :max_month  \n"
			+"      AND year(history_out)=2016 \n"
			+"      AND result_id=5) dead \n"
			;
	public static String bedDayH2 = ""
			+" SELECT calc3.*, "
			+"        round(bed_occupancy/treat_avg,2) bed_turnover "
			+" FROM "
			+"   (SELECT calc2.*, "
			+"           bed_day*100/bed_day_plan bed_day_fulfil, "
			+"           bed_day*10/DEPARTMENT_BED*.1 bed_occupancy, "
			+"           TREAT*1000/bed_day*.1 treat_avg "
			+"    FROM "
			+"      (SELECT calc1.*, "
			+"              ifnull(dead,0)*1000/TREAT*.1 mortality_procent, "
			+"                                        ym.k*100/ym.yd*ym.month_days*DEPARTMENT_bed/100 bed_day_plan "
			+"       FROM "
			+"         (SELECT d.DEPARTMENT_id dp_id, "
			+"                 d.DEPARTMENT_name, "
			+"                 d.DEPARTMENT_bed, "
			+"                 summ.*, "
			+"                 (summ.in_clinic+ifnull(summ.in_department,0)+ summ.out_clinic + ifnull(summ.out_department,0) + ifnull(summ.dead,0))/2 TREAT "
			+"          FROM hol2.DEPARTMENT d "
			+"          LEFT JOIN "
			+"            (SELECT sum(MOVEDEPARTMENTPATIENT_IN) in_clinic, "
			+"                    sum(MOVEDEPARTMENTPATIENT_INDEPARTMENT) in_department, "
			+"                    sum(MOVEDEPARTMENTPATIENT_OUT) out_clinic, "
			+"                    sum(MOVEDEPARTMENTPATIENT_OUTDEPARTMENT) out_department, "
			+"                    sum(MOVEDEPARTMENTPATIENT_DEAD) dead, "
			+"                    sum(MOVEDEPARTMENTPATIENT_PATIENT2DAY) bed_day, "
			+"                    DEPARTMENT_ID "
			+"             FROM hol2.MOVEDEPARTMENTPATIENT "
			+"             WHERE month(MOVEDEPARTMENTPATIENT_DATE)>=1 "
			+"               AND month(MOVEDEPARTMENTPATIENT_DATE)<=1 "
			+"               AND year(MOVEDEPARTMENTPATIENT_DATE)=2016 "
			+"             GROUP BY DEPARTMENT_ID) summ ON d.DEPARTMENT_id=summ.DEPARTMENT_id "
			+"          ORDER BY d.DEPARTMENT_id) calc1, "
			+"         (SELECT day(dateadd(dd,-day(ym_next),ym_next)) month_days, "
			+"                 CASE WHEN 2016%4 =0 THEN 366 ELSE 365 END yd, "
			+"                                                           280 k "
			+"          FROM "
			+"            (SELECT DATEADD(m,1,ym) ym_next "
			+"             FROM "
			+"               (SELECT parsedatetime(concat(2016,'-',1,'-1'),'yyyy-MM-dd') ym)ym)ym)ym) calc2)calc3 "
;

}
