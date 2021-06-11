package com.fsega.timetable.config.ldap;

import java.util.Optional;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

import com.fsega.timetable.model.enums.Role;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class LdapUserRepository {

    @Autowired
    private LdapTemplate ldapTemplate;

    public Optional<LdapUser> findByUsernameAndPassword(String username, String password) {
        return ldapTemplate.search(
                query().where("objectclass").is("inetOrgPerson")
                        .and("cn").is(username)
                        .and("userpassword").is(password),
                new PersonAttributesMapper())
                .stream()
                .findFirst();
    }

    public Optional<LdapUser> findTeacherByUsername(String username) {
        return ldapTemplate.search(
                query().where("objectclass").is("inetOrgPerson")
                        .and("cn").is(username)
                        .and("title").is("TEACHER"),
                new PersonAttributesMapper())
                .stream()
                .findFirst();
    }

    private class PersonAttributesMapper implements AttributesMapper<LdapUser> {
        public LdapUser mapFromAttributes(Attributes attrs) throws NamingException {
            return LdapUser.builder()
                    .username(getProp(attrs, "cn"))
                    .firstName(getProp(attrs, "givenname"))
                    .lastName(getProp(attrs, "sn"))
                    .email(getProp(attrs, "mail"))
                    .role(Role.valueOf(getProp(attrs, "title")))
                    .specializationId(getOptionalProp(attrs, "ou")
                            .map(String.class::cast)
                            .orElse(null))
                    .studyYear(getOptionalProp(attrs, "employeeNumber")
                            .map(String.class::cast)
                            .map(Integer::valueOf)
                            .orElse(null))
                    .build();
        }
    }

    private String getProp(Attributes attrs, String name) throws NamingException {
        return (String) attrs.get(name).get();
    }

    private Optional<Object> getOptionalProp(Attributes attrs, String name) throws NamingException {
        Attribute attribute = attrs.get(name);
        if (attribute != null) {
            return Optional.ofNullable(attribute.get());
        }
        return Optional.empty();
    }

}