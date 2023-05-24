package com.example.workdays.adapters.api.v1;

import com.example.workdays.domain.usecases.persistence.ExportNonWorkingDaysUseCase;
import com.example.workdays.domain.usecases.persistence.ImportNonWorkingDaysUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NonWorkingDaysPersister {
    private final ImportNonWorkingDaysUseCase importNonWorkingDaysUseCase;
    private final ExportNonWorkingDaysUseCase exportNonWorkingDaysUseCase;

    public void loadData() {
        importNonWorkingDaysUseCase.importData();
    }

    public void saveData() {
        exportNonWorkingDaysUseCase.exportData();
    }

}
