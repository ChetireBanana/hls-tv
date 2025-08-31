package com.example.hls_tv.domain

import com.example.hls_tv.data.model.Channel
import com.example.hls_tv.data.model.ChannelGroup
import com.example.hls_tv.data.repository.ChannelsRepository

import java.util.UUID
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

class ParseChannelsUseCase @Inject constructor(
    private val channelsRepository: ChannelsRepository
) {

    suspend fun execute(): List<ChannelGroup> {
        val channels = mutableListOf<Channel>()

        var id: String? = null
        var name: String = ""
        var logo: String? = null
        var group: String? = null

        val playlistText = channelsRepository.getChannels()

        playlistText.lines().forEach { line ->
            when {
                line.startsWith("#EXTINF") -> {
                    id = Regex("""tvg-id="(.*?)"""").find(line)?.groupValues?.get(1)
                    logo = Regex("""tvg-logo="(.*?)"""").find(line)?.groupValues?.get(1)
                    group = Regex("""group-title="(.*?)"""").find(line)?.groupValues?.get(1)
                    name = line.substringAfter(",").trim()
                }
                line.startsWith("http") -> {
                    val finalId = id ?: UUID.randomUUID().toString()
                    channels.add(Channel(finalId, name, logo, group, line))
                }
            }
        }

        return groupChannels(channels)
    }

    fun groupChannels(channels: List<Channel>): List<ChannelGroup> {
        val groupedChannels = channels.groupBy { it.group }
        return groupedChannels.map { (group, channels) ->
            ChannelGroup(group, channels)
        }
    }
}