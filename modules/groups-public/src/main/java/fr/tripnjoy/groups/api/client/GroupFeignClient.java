package fr.tripnjoy.groups.api.client;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.common.dto.ModelWithEmail;
import fr.tripnjoy.groups.dto.request.*;
import fr.tripnjoy.groups.dto.response.GroupInfoModel;
import fr.tripnjoy.groups.dto.response.GroupMemberModel;
import fr.tripnjoy.groups.dto.response.GroupMemoriesResponse;
import fr.tripnjoy.groups.dto.response.GroupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "SERVICE-GROUPS", contextId = "SERVICE-GROUPS-GROUPS", path = "/groups")
public interface GroupFeignClient {
    @GetMapping("{id}")
    Collection<GroupResponse> getUserGroups(@PathVariable("id") final long userId);

    @GetMapping("open")
    Collection<Long> getOpenGroups();

    @GetMapping("invites/{id}")
    Collection<GroupResponse> getUserInvites(@PathVariable("id") final long userId);

    @GetMapping("isInGroup")
    BooleanResponse isUserInGroup(@RequestParam("groupId") long groupId, @RequestParam("userId") long userId);

    @GetMapping("info/{id}")
    GroupInfoModel getInfo(@PathVariable("id") final long groupId);

    @GetMapping("{id}/members")
    List<Long> getMembers(@PathVariable("id") final long groupId);

    @DeleteMapping("{group}/user/")
    void leaveGroup(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId);

    @GetMapping("{groupId}/users/{userId}")
    GroupMemberModel getMember(@PathVariable("groupId") final long groupId, @PathVariable("userId") final long userId,
                                      @RequestHeader("userId") final long currentUserId);

    @PostMapping("private/")
    GroupResponse createPrivateGroup(@RequestHeader("userId") final long userId, @RequestBody CreatePrivateGroupRequest createPrivateGroupRequest);

    @PostMapping("private/{group}/user")
    void inviteUserInPrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username, @RequestBody ModelWithEmail model);

    @DeleteMapping("private/{group}/user/{id}")
    void removeUserFromPrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username, @PathVariable("id") final long userId);

    @PatchMapping("private/{group}")
    void updatePrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username, @RequestBody UpdatePrivateGroupRequest updatePrivateGroupRequest);

    @PatchMapping("{group}")
    void updatePublicGroup(@PathVariable("group") final long groupId, @RequestBody UpdatePublicGroupRequest request);

    @DeleteMapping("private/{group}")
    void deletePrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username);

    @PatchMapping("{group}/join/")
    void joinGroup(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId);

    @PatchMapping("private/{group}/join/")
    void joinGroupWithoutInvite(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId, @RequestBody JoinGroupWithoutInviteModel model);

    @PatchMapping("{group}/decline/")
    void declineGroupInvite(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId);

    @GetMapping("{groupId}/memories")
    GroupMemoriesResponse getMemories(@PathVariable("groupId") final long groupId);

    @PostMapping("{groupId}/memories")
    GroupMemoriesResponse addMemory(@PathVariable("groupId") final long groupId, @RequestBody GroupMemoryRequest memoryCreationRequest);

    @GetMapping("private/{group}/qrcode")
    String getQRCode(@PathVariable("group") final long groupId, @RequestHeader("userId") long userId);

    @PostMapping("/")
    GroupResponse createPublicGroup(@RequestHeader("roles") List<String> roles, @RequestBody CreatePublicGroupRequest request);
}
