package fr.tobby.tripnjoyback.service;


import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.repository.GroupMemberRepository;
import org.springframework.security.core.context.SecurityContextHolder;

public class MemberCheckerService {
    protected final GroupMemberRepository groupMemberRepository;

    public MemberCheckerService(GroupMemberRepository groupMemberRepository) {
        this.groupMemberRepository = groupMemberRepository;
    }

    public void checkMember(long groupId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!groupMemberRepository.findByGroupId(groupId).stream().anyMatch(m -> m.getUser().getEmail().equals(email)));
            throw new ForbiddenOperationException("You cannot perform this operation");
    }
}
