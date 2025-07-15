package org.onlybuns.service;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.onlybuns.repository.UserRepository;
import org.onlybuns.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BloomFilterService {

    private final BloomFilter<String> usernameBloomFilter;

    public BloomFilterService(UserRepository userRepository) {
        // Initialize the Bloom filter with a size and false positive rate
        this.usernameBloomFilter = BloomFilter.create(
                Funnels.stringFunnel(Charsets.UTF_8),
                100000, // Expected number of usernames
                0.01    // False positive probability
        );

        // Populate the Bloom filter with existing usernames
        List<String> existingUsernames = userRepository.findAll().stream()
                .map(User::getUsername) // Extract the username from each User object
                .toList();

        for (String username : existingUsernames) {
            usernameBloomFilter.put(username);
        }
    }

    public BloomFilter<String> getUsernameBloomFilter() {
        return usernameBloomFilter;
    }
}
