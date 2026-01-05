package pe.com.practicar.repository.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ZoneSummaryByLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer securityLevel;
    
    private Long count;
}
