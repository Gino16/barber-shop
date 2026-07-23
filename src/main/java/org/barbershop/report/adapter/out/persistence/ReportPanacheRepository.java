package org.barbershop.report.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReportPanacheRepository implements PanacheRepository<DailyReportJpaEntity> {

}
