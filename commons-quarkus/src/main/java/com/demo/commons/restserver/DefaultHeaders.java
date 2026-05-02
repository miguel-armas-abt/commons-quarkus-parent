package com.demo.commons.restserver;

import com.demo.commons.tracing.enums.ForwardedParam;
import com.demo.commons.tracing.enums.TraceParam;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultHeaders implements Serializable {

    @HeaderParam("channelId")
    @Pattern(regexp = ForwardedParam.Constants.CHANNEL_ID_REGEX)
    @NotBlank
    private String channelId;

    @HeaderParam("traceParent")
    @Pattern(regexp = TraceParam.Constants.TRACE_PARENT_REGEX)
    @NotBlank
    private String traceParent;
}