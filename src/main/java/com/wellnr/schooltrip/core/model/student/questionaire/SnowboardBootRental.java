package com.wellnr.schooltrip.core.model.student.questionaire;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class SnowboardBootRental implements BootRental {

    /**
     * Boot size in German boot sizes.
     */
    int size;

}
