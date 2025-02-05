package com.featureprobe.api.controller;

import com.featureprobe.api.base.doc.CreateApiResponse;
import com.featureprobe.api.base.doc.DefaultApiResponses;
import com.featureprobe.api.base.doc.GetApiResponse;
import com.featureprobe.api.base.doc.PatchApiResponse;
import com.featureprobe.api.base.hook.Action;
import com.featureprobe.api.base.hook.Hook;
import com.featureprobe.api.base.hook.Resource;
import com.featureprobe.api.dto.EnvironmentCreateRequest;
import com.featureprobe.api.dto.EnvironmentQueryRequest;
import com.featureprobe.api.dto.EnvironmentResponse;
import com.featureprobe.api.dto.EnvironmentUpdateRequest;
import com.featureprobe.api.base.enums.ResponseCodeEnum;
import com.featureprobe.api.base.enums.ValidateTypeEnum;
import com.featureprobe.api.base.model.BaseResponse;
import com.featureprobe.api.service.EnvironmentService;
import com.featureprobe.api.service.IncludeArchivedEnvironmentService;
import com.featureprobe.api.validate.ResourceExistsValidate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/projects/{projectKey}/environments")
@DefaultApiResponses
@ResourceExistsValidate
@Tag(name = "Environment", description = "Using the environments API, you can create, update, and manage environments")
public class EnvironmentController {

    private EnvironmentService environmentService;

    private IncludeArchivedEnvironmentService includeArchivedEnvironmentService;

    @PostMapping
    @CreateApiResponse
    @Operation(summary = "Create environment", description = "Create a new environment.")
    @Hook(resource = Resource.ENVIRONMENT, action = Action.CREATE)
    public EnvironmentResponse create(@PathVariable("projectKey") String projectKey,
                                      @RequestBody @Validated EnvironmentCreateRequest createRequest) {
        includeArchivedEnvironmentService.validateIncludeArchivedEnvironmentByKey(projectKey, createRequest.getKey());
        includeArchivedEnvironmentService.validateIncludeArchivedEnvironmentByName(projectKey, createRequest.getName());
        return environmentService.create(projectKey, createRequest);
    }

    @PatchMapping("/{environmentKey}")
    @PatchApiResponse
    @Operation(summary = "Update environment", description = "Update a environment.")
    @Hook(resource = Resource.ENVIRONMENT, action = Action.UPDATE)
    public EnvironmentResponse update(@PathVariable("projectKey") String projectKey,
                                      @PathVariable("environmentKey") String environmentKey,
                                      @RequestBody @Validated EnvironmentUpdateRequest updateRequest) {
        return environmentService.update(projectKey, environmentKey, updateRequest);
    }

    @PatchMapping("/{environmentKey}/offline")
    @PatchApiResponse
    @Operation(summary = "Offline environment", description = "Offline a environment.")
    @Hook(resource = Resource.ENVIRONMENT, action = Action.OFFLINE)
    public EnvironmentResponse offline(@PathVariable("projectKey") String projectKey,
                                       @PathVariable("environmentKey") String environmentKey) {
        return environmentService.offline(projectKey, environmentKey);
    }

    @PatchMapping("/{environmentKey}/restore")
    @PatchApiResponse
    @Operation(summary = "Restore environment", description = "Restore a offline environment.")
    @Hook(resource = Resource.ENVIRONMENT, action = Action.RESTORE)
    public EnvironmentResponse restore(@PathVariable("projectKey") String projectKey,
                                       @PathVariable("environmentKey") String environmentKey) {
        return environmentService.restore(projectKey, environmentKey);
    }

    @GetMapping
    @GetApiResponse
    @Operation(summary = "List environment", description = "Get a list of all environment")
    public List<EnvironmentResponse> list(@PathVariable("projectKey") String projectKey,
                                          EnvironmentQueryRequest queryRequest) {
        return environmentService.list(projectKey, queryRequest);
    }

    @GetMapping("/{environmentKey}")
    @GetApiResponse
    @Operation(summary = "Query environment", description = "Query a environment.")
    public EnvironmentResponse query(@PathVariable("projectKey") String projectKey,
                                     @PathVariable("environmentKey") String environmentKey) {
        return environmentService.query(projectKey, environmentKey);
    }

    @GetMapping("/exists")
    @GetApiResponse
    @Operation(summary = "Check environment exist", description = "Check environment exist")
    public BaseResponse exists(@PathVariable("projectKey") String projectKey,
                               @RequestParam ValidateTypeEnum type,
                               @RequestParam String value) {
        includeArchivedEnvironmentService.validateIncludeArchivedEnvironment(projectKey, type, value);
        return new BaseResponse(ResponseCodeEnum.SUCCESS);
    }

}
