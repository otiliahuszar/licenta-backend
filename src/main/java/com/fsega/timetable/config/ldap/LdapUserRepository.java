package com.fsega.timetable.config.ldap;

import java.util.List;
import java.util.Optional;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

import com.fsega.timetable.exception.NotFoundException;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class LdapUserRepository {

    @Autowired
    private LdapTemplate ldapTemplate;

    public List<LdapUser> getAllPersons() {
        return ldapTemplate.search(
                query()
                        .where("objectclass").is("inetOrgPerson"),
                new PersonAttributesMapper());
    }

    public LdapUser getByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " was not found"));
    }

    public Optional<LdapUser> findByUsername(String username) {
        return ldapTemplate.search(
                query()
                        .where("objectclass").is("inetOrgPerson")
                        .and("cn").is(username),
                new PersonAttributesMapper())
                .stream()
                .findFirst();
    }

    private class PersonAttributesMapper implements AttributesMapper<LdapUser> {
        public LdapUser mapFromAttributes(Attributes attrs) throws NamingException {
            LdapUser person = new LdapUser();
            person.setUsername((String) attrs.get("cn").get());
            person.setPassword(String.valueOf(attrs.get("userpassword").get()));
            person.setFirstName((String) attrs.get("givenname").get());
            person.setLastName((String) attrs.get("sn").get());
            return person;
        }
    }

}